"""MinIO service layer for file storage operations."""

import logging
import os
import uuid
from datetime import timedelta
from urllib.parse import urlparse

from django.conf import settings
from django.utils import timezone
from minio import Minio
from minio.error import S3Error

from apps.core.exceptions import StorageException

logger = logging.getLogger(__name__)


def get_minio_client():
    """Create and return a configured MinIO client.

    Parses MINIO_ENDPOINT setting to extract host:port and detect secure mode.
    """
    parsed = urlparse(settings.MINIO_ENDPOINT)
    endpoint = parsed.netloc or parsed.path
    secure = parsed.scheme == "https"

    return Minio(
        endpoint,
        access_key=settings.MINIO_ACCESS_KEY,
        secret_key=settings.MINIO_SECRET_KEY,
        secure=secure,
    )


def ensure_bucket():
    """Ensure the configured bucket exists, creating it if necessary."""
    client = get_minio_client()
    bucket = settings.MINIO_BUCKET_NAME
    if not client.bucket_exists(bucket):
        client.make_bucket(bucket)


def generate_storage_key(filename, categoria=None):
    """Generate a hierarchical storage key for MinIO.

    Pattern: [categoria/]yyyy/MM/dd/uuid.ext
    """
    now = timezone.now()
    ext = os.path.splitext(filename)[1].lower()
    unique_name = f"{uuid.uuid4()}{ext}"
    date_path = now.strftime("%Y/%m/%d")

    if categoria:
        return f"{categoria}/{date_path}/{unique_name}"
    return f"{date_path}/{unique_name}"


def upload_file(uploaded_file, storage_key):
    """Upload a file to MinIO.

    Raises StorageException on failure.
    """
    client = get_minio_client()
    bucket = settings.MINIO_BUCKET_NAME

    try:
        ensure_bucket()
        uploaded_file.seek(0)
        client.put_object(
            bucket,
            storage_key,
            uploaded_file,
            uploaded_file.size,
            content_type=uploaded_file.content_type,
        )
    except S3Error as exc:
        logger.error("MinIO upload failed for key %s: %s", storage_key, exc)
        raise StorageException(f"Falha ao enviar arquivo: {exc}") from exc


def download_file(storage_key):
    """Download a file from MinIO and return its bytes.

    Raises StorageException on failure.
    """
    client = get_minio_client()
    bucket = settings.MINIO_BUCKET_NAME
    response = None

    try:
        response = client.get_object(bucket, storage_key)
        return response.read()
    except S3Error as exc:
        logger.error("MinIO download failed for key %s: %s", storage_key, exc)
        raise StorageException(f"Falha ao baixar arquivo: {exc}") from exc
    finally:
        if response is not None:
            response.close()
            response.release_conn()


def get_presigned_url(storage_key, expires_minutes=60):
    """Generate a presigned URL for direct download from MinIO."""
    client = get_minio_client()
    bucket = settings.MINIO_BUCKET_NAME

    try:
        return client.presigned_get_object(
            bucket,
            storage_key,
            expires=timedelta(minutes=expires_minutes),
        )
    except S3Error as exc:
        logger.error("MinIO presigned URL failed for key %s: %s", storage_key, exc)
        raise StorageException(f"Falha ao gerar URL: {exc}") from exc


def delete_file(storage_key):
    """Delete a file from MinIO (best effort, silently catches errors)."""
    client = get_minio_client()
    bucket = settings.MINIO_BUCKET_NAME

    try:
        client.remove_object(bucket, storage_key)
    except S3Error as exc:
        logger.warning("MinIO delete failed for key %s: %s", storage_key, exc)

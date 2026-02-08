"""File validation utilities for upload requests."""

import os

from apps.core.exceptions import InvalidFileException

ALLOWED_CONTENT_TYPES = {
    "image/jpeg",
    "image/png",
    "image/gif",
    "application/pdf",
    "application/msword",
    "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
}

ALLOWED_EXTENSIONS = {"jpg", "jpeg", "png", "gif", "pdf", "doc", "docx"}

MAX_FILE_SIZE = 10 * 1024 * 1024  # 10MB


def validate_file(uploaded_file):
    """Validate an uploaded file for type, size, and name safety.

    Raises InvalidFileException on any validation failure.
    """
    if not uploaded_file or uploaded_file.size == 0:
        raise InvalidFileException("Arquivo vazio ou nao enviado.")

    filename = getattr(uploaded_file, "name", None) or ""
    if not filename.strip():
        raise InvalidFileException("Nome do arquivo e obrigatorio.")

    if ".." in filename or "/" in filename or "\\" in filename or "\0" in filename:
        raise InvalidFileException("Nome do arquivo contem caracteres invalidos.")

    if uploaded_file.size > MAX_FILE_SIZE:
        raise InvalidFileException(f"Arquivo excede o tamanho maximo de {MAX_FILE_SIZE // (1024 * 1024)}MB.")

    content_type = getattr(uploaded_file, "content_type", None) or ""
    if content_type not in ALLOWED_CONTENT_TYPES:
        raise InvalidFileException(f"Tipo de arquivo nao permitido: {content_type}")

    ext = os.path.splitext(filename)[1].lstrip(".").lower()
    if ext not in ALLOWED_EXTENSIONS:
        raise InvalidFileException(f"Extensao de arquivo nao permitida: {ext}")

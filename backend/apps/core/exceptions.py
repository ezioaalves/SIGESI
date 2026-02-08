"""Custom DRF exception handler and exception classes."""

from rest_framework import status
from rest_framework.exceptions import (
    APIException,
    ValidationError,
)
from rest_framework.views import exception_handler


class NotFoundException(APIException):
    """Resource not found (404)."""

    status_code = status.HTTP_404_NOT_FOUND
    default_detail = "Recurso nao encontrado."
    default_code = "not_found"


class ConflictException(APIException):
    """Conflict error (409)."""

    status_code = status.HTTP_409_CONFLICT
    default_detail = "Conflito."
    default_code = "conflict"


class InvalidFileException(APIException):
    """Invalid file error (400)."""

    status_code = status.HTTP_400_BAD_REQUEST
    default_detail = "Arquivo invalido."
    default_code = "invalid_file"


class StorageException(APIException):
    """Storage error (500)."""

    status_code = status.HTTP_500_INTERNAL_SERVER_ERROR
    default_detail = "Erro no armazenamento."
    default_code = "storage_error"


def _extract_first_message(detail):
    """Extract the first error message from DRF ValidationError detail."""
    if isinstance(detail, str):
        return detail
    if isinstance(detail, list):
        for item in detail:
            msg = _extract_first_message(item)
            if msg:
                return msg
    if isinstance(detail, dict):
        for value in detail.values():
            msg = _extract_first_message(value)
            if msg:
                return msg
    return str(detail)


def _error_label(status_code):
    """Map status code to error label matching Spring Boot format."""
    labels = {
        400: "Bad Request",
        401: "Unauthorized",
        403: "Forbidden",
        404: "Not Found",
        405: "Method Not Allowed",
        409: "Conflito",
        413: "Payload Too Large",
        429: "Too Many Requests",
        500: "Internal Server Error",
    }
    return labels.get(status_code, "Error")


def custom_exception_handler(exc, context):
    """Return standardized error responses matching Spring Boot format.

    Response format: {"status": code, "error": "type", "message": "detail"}
    """
    response = exception_handler(exc, context)

    if response is None:
        return None

    code = response.status_code

    if isinstance(exc, ValidationError):
        message = _extract_first_message(exc.detail)
    elif isinstance(exc, APIException):
        message = str(exc.detail)
    else:
        message = str(exc)

    response.data = {
        "status": code,
        "error": _error_label(code),
        "message": message,
    }

    return response

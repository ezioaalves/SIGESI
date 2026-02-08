"""Comentario URL routing."""

from rest_framework.routers import DefaultRouter

from apps.comentarios.views import ComentarioViewSet

router = DefaultRouter()
router.register("", ComentarioViewSet, basename="comentario")

urlpatterns = router.urls

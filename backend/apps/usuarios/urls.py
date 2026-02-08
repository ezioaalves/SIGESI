"""Usuario URL routing."""

from rest_framework.routers import DefaultRouter

from apps.usuarios.views import UsuarioViewSet

router = DefaultRouter()
router.register("", UsuarioViewSet, basename="usuario")

urlpatterns = router.urls

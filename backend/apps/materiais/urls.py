"""Material URL routing."""

from rest_framework.routers import DefaultRouter

from apps.materiais.views import MaterialViewSet

router = DefaultRouter()
router.register("", MaterialViewSet, basename="material")

urlpatterns = router.urls

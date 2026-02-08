"""Cemiterio URL routing."""

from rest_framework.routers import DefaultRouter

from apps.cemiterios.views import CemiterioViewSet

router = DefaultRouter()
router.register("", CemiterioViewSet, basename="cemiterio")

urlpatterns = router.urls

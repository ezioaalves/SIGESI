"""Gaveta FilterSet for django-filter."""

import django_filters

from apps.gavetas.models import Gaveta


class GavetaFilter(django_filters.FilterSet):
    """FilterSet for Gaveta with jazigo_id and ocupante_id filters."""

    jazigo_id = django_filters.NumberFilter(field_name="jazigo__id")
    ocupante_id = django_filters.NumberFilter(field_name="ocupante__id")

    class Meta:
        """Meta options."""

        model = Gaveta
        fields = ["jazigo_id", "ocupante_id"]

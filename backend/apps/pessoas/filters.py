"""Pessoa FilterSet for django-filter."""

import django_filters

from apps.pessoas.models import Pessoa, SexoEnum


class PessoaFilter(django_filters.FilterSet):
    """FilterSet for Pessoa with nome, cpf, sexo, and endereco_id filters."""

    nome = django_filters.CharFilter(lookup_expr="icontains")
    cpf = django_filters.CharFilter(lookup_expr="exact")
    sexo = django_filters.ChoiceFilter(choices=SexoEnum.choices)
    endereco_id = django_filters.NumberFilter(field_name="endereco__id")

    class Meta:
        """Meta options."""

        model = Pessoa
        fields = ["nome", "cpf", "sexo", "endereco_id"]

"""Pessoa ViewSet with filtering and CPF lookup."""

from rest_framework import status
from rest_framework.decorators import action
from rest_framework.response import Response
from rest_framework.viewsets import ModelViewSet

from apps.core.exceptions import NotFoundException
from apps.core.permissions import IsAllRoles
from apps.enderecos.models import Endereco
from apps.pessoas.filters import PessoaFilter
from apps.pessoas.models import Pessoa
from apps.pessoas.serializers import (
    PessoaCreateSerializer,
    PessoaResponseSerializer,
    PessoaUpdateSerializer,
)


class PessoaViewSet(ModelViewSet):
    """ViewSet for Pessoa CRUD with filtering and CPF lookup."""

    queryset = Pessoa.objects.all().order_by("id")
    permission_classes = [IsAllRoles]
    filterset_class = PessoaFilter

    def get_serializer_class(self):
        """Return the appropriate serializer per action."""
        if self.action == "create":
            return PessoaCreateSerializer
        if self.action in ("update", "partial_update"):
            return PessoaUpdateSerializer
        return PessoaResponseSerializer

    def create(self, request, *args, **kwargs):
        """Create a new Pessoa with CPF uniqueness check."""
        serializer = self.get_serializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        pessoa = serializer.save()
        response_serializer = PessoaResponseSerializer(pessoa)
        return Response(response_serializer.data, status=status.HTTP_201_CREATED)

    def partial_update(self, request, *args, **kwargs):
        """Update Pessoa fields individually."""
        pessoa = self.get_object()
        serializer = self.get_serializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        data = serializer.validated_data

        if "nome" in data:
            pessoa.nome = data["nome"]
        if "cpf" in data:
            pessoa.cpf = data["cpf"]
        if "sexo" in data:
            pessoa.sexo = data["sexo"]
        if "endereco_id" in data:
            if data["endereco_id"] is None:
                pessoa.endereco = None
            else:
                pessoa.endereco = Endereco.objects.get(pk=data["endereco_id"])

        pessoa.save()
        response_serializer = PessoaResponseSerializer(pessoa)
        return Response(response_serializer.data)

    @action(
        detail=False,
        methods=["get"],
        url_path="cpf",
        url_name="by-cpf",
    )
    def by_cpf(self, request):
        """Look up a Pessoa by CPF."""
        cpf = request.query_params.get("cpf")
        if not cpf:
            return Response(
                {"status": 400, "error": "Bad Request", "message": "cpf e obrigatorio."},
                status=status.HTTP_400_BAD_REQUEST,
            )
        try:
            pessoa = Pessoa.objects.get(cpf=cpf)
        except Pessoa.DoesNotExist as err:
            raise NotFoundException("Pessoa nao encontrada.") from err
        response_serializer = PessoaResponseSerializer(pessoa)
        return Response(response_serializer.data)

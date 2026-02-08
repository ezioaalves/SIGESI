"""PDF generation service for Documentos using WeasyPrint."""

import logging
from pathlib import Path

from django.conf import settings
from weasyprint import HTML

from apps.core.exceptions import StorageException

logger = logging.getLogger(__name__)

MESES_PT_BR = {
    1: "janeiro",
    2: "fevereiro",
    3: "marco",
    4: "abril",
    5: "maio",
    6: "junho",
    7: "julho",
    8: "agosto",
    9: "setembro",
    10: "outubro",
    11: "novembro",
    12: "dezembro",
}


def _format_date_oficio(date_obj):
    """Format date for oficio: 'Pau dos Ferros/RN, dd de MMMM de yyyy'."""
    month_name = MESES_PT_BR[date_obj.month]
    return f"Pau dos Ferros/RN, {date_obj.day} de {month_name} de {date_obj.year}"


def _format_date_memorando(date_obj):
    """Format date for memorando: 'dd de MMMM de yyyy'."""
    month_name = MESES_PT_BR[date_obj.month]
    return f"{date_obj.day} de {month_name} de {date_obj.year}"


def _get_background_image_url():
    """Get the absolute file URL for the background image."""
    image_path = Path(settings.BASE_DIR) / "static" / "images" / "backgroundimage.jpeg"
    return image_path.as_uri()


def _build_oficio_html(documento):
    """Build HTML string for an oficio document."""
    date_str = _format_date_oficio(documento.data)
    bg_url = _get_background_image_url()

    body_paragraphs = ""
    for paragraph in documento.body.split("\n"):
        stripped = paragraph.strip()
        if stripped:
            body_paragraphs += f"<p>{stripped}</p>\n"

    return f"""<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<style>
  @page {{
    size: A4;
    margin: 0;
    background-image: url("{bg_url}");
    background-size: 210mm 297mm;
    background-position: center;
    background-repeat: no-repeat;
  }}
  body {{
    font-family: "Liberation Sans", Arial, Helvetica, sans-serif;
    font-size: 10pt;
    line-height: 14pt;
    text-align: justify;
    padding: 145pt 60pt 80pt 75pt;
    margin: 0;
  }}
  .header {{
    text-align: center;
    font-weight: bold;
    font-size: 10pt;
    margin-bottom: 20pt;
  }}
  .date {{
    text-align: right;
    margin-bottom: 15pt;
  }}
  .greeting {{
    margin-bottom: 15pt;
  }}
  .subject {{
    font-weight: bold;
    margin-bottom: 15pt;
  }}
  .honorifico {{
    margin-bottom: 10pt;
  }}
  .body-text p {{
    text-indent: 40pt;
    margin-bottom: 6pt;
  }}
  .closing {{
    text-align: center;
    margin-top: 20pt;
    margin-bottom: 30pt;
  }}
  .signature {{
    text-align: center;
    margin-top: 40pt;
  }}
  .signature-line {{
    text-align: center;
    margin-bottom: 5pt;
  }}
  .signature-name {{
    font-weight: bold;
    text-align: center;
  }}
  .signature-title {{
    text-align: center;
    font-size: 9pt;
  }}
  .portaria {{
    text-align: center;
    font-size: 9pt;
  }}
</style>
</head>
<body>
  <div class="header">OFICIO N&ordm; {documento.numero}</div>
  <div class="date">{date_str}</div>
  <div class="greeting">Ao {documento.interessado},</div>
  <div class="subject">ASSUNTO: {documento.subject}</div>
  {"<div class='honorifico'>" + documento.honorifico + "</div>" if documento.honorifico else ""}
  <div class="body-text">
    {body_paragraphs}
  </div>
  <div class="closing">Atenciosamente,</div>
  <div class="signature">
    <div class="signature-line">________________________________________</div>
    <div class="signature-name">{documento.assinante}</div>
    <div class="signature-title">Secret&aacute;rio Municipal de Infraestrutura &ndash; SEINFRA</div>
    {"<div class='portaria'>" + documento.portaria + "</div>" if documento.portaria else ""}
  </div>
</body>
</html>"""


def _build_memorando_html(documento):
    """Build HTML string for a memorando document."""
    date_str = _format_date_memorando(documento.data)
    bg_url = _get_background_image_url()

    body_paragraphs = ""
    for paragraph in documento.body.split("\n"):
        stripped = paragraph.strip()
        if stripped:
            body_paragraphs += f"<p>{stripped}</p>\n"

    return f"""<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<style>
  @page {{
    size: A4;
    margin: 0;
    background-image: url("{bg_url}");
    background-size: 210mm 297mm;
    background-position: center;
    background-repeat: no-repeat;
  }}
  body {{
    font-family: "Liberation Sans", Arial, Helvetica, sans-serif;
    font-size: 10pt;
    line-height: 14pt;
    text-align: justify;
    padding: 145pt 60pt 80pt 75pt;
    margin: 0;
  }}
  .header {{
    text-align: center;
    font-weight: bold;
    font-size: 10pt;
    margin-bottom: 10pt;
  }}
  .destino {{
    font-weight: bold;
    margin-bottom: 10pt;
  }}
  .date {{
    text-align: right;
    margin-bottom: 15pt;
  }}
  .subject {{
    font-weight: bold;
    margin-bottom: 15pt;
  }}
  .honorifico {{
    margin-bottom: 10pt;
  }}
  .body-text p {{
    text-indent: 40pt;
    margin-bottom: 6pt;
  }}
  .closing {{
    text-align: center;
    margin-top: 20pt;
    margin-bottom: 30pt;
  }}
  .signature {{
    text-align: center;
    margin-top: 40pt;
  }}
  .signature-line {{
    text-align: center;
    margin-bottom: 5pt;
  }}
  .signature-name {{
    font-weight: bold;
    text-align: center;
  }}
  .signature-title {{
    text-align: center;
    font-size: 9pt;
  }}
  .portaria {{
    text-align: center;
    font-size: 9pt;
  }}
  .reception {{
    margin-top: 40pt;
    width: 100%;
  }}
  .reception td {{
    padding: 5pt;
    font-size: 9pt;
    width: 50%;
  }}
</style>
</head>
<body>
  <div class="header">MEMORANDO N&ordm; {documento.numero}</div>
  <div class="destino">DESTINO: {documento.interessado}</div>
  <div class="date">{date_str}</div>
  <div class="subject">ASSUNTO: {documento.subject}</div>
  {"<div class='honorifico'>" + documento.honorifico + "</div>" if documento.honorifico else ""}
  <div class="body-text">
    {body_paragraphs}
  </div>
  <div class="closing">Atenciosamente,</div>
  <div class="signature">
    <div class="signature-line">________________________________________</div>
    <div class="signature-name">{documento.assinante}</div>
    <div class="signature-title">Secret&aacute;rio Municipal de Infraestrutura &ndash; SEINFRA</div>
    {"<div class='portaria'>" + documento.portaria + "</div>" if documento.portaria else ""}
  </div>
  <table class="reception">
    <tr>
      <td>Recebido por:___________________________</td>
      <td>Em(data/hor&aacute;rio):_____________________</td>
    </tr>
  </table>
</body>
</html>"""


def generate_documento_pdf(documento):
    """Generate a PDF for a Documento.

    Returns PDF bytes.
    Raises StorageException on failure.
    """
    try:
        if documento.tipo == "MEMORANDO":
            html_content = _build_memorando_html(documento)
        else:
            html_content = _build_oficio_html(documento)

        base_url = str(settings.BASE_DIR) + "/"
        return HTML(string=html_content, base_url=base_url).write_pdf()
    except Exception as exc:
        logger.error("PDF generation failed for documento %s: %s", documento.id, exc)
        raise StorageException(f"Falha ao gerar PDF: {exc}") from exc

package com.sigesi.sigesi.documentos;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
public class DocumentoPdfService {

  public byte[] gerarPdfDocumento(Documento doc) {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Document document = new Document(PageSize.A4, 75, 60, 145, 80);

    try {
      PdfWriter writer = PdfWriter.getInstance(document, out);
      document.open();

      adicionarBackground(writer);

      Font fontBold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
      Font fontNormal = FontFactory.getFont(FontFactory.HELVETICA, 10);

      if (DocumentoTipo.OFICIO.equals(doc.getTipo())) {
        montarLayoutOficio(document, doc, fontBold, fontNormal);
      } else {
        montarLayoutMemorando(document, doc, fontBold, fontNormal);
      }

      document.add(new Paragraph(doc.getHonorifico(), fontNormal));
      document.add(new Paragraph("\n"));

      Paragraph pBody = new Paragraph(doc.getBody(), fontNormal);
      pBody.setAlignment(Element.ALIGN_JUSTIFIED);
      pBody.setLeading(14f);
      document.add(pBody);
      document.add(new Paragraph("\n"));

      document.add(new Paragraph("Atenciosamente,", fontNormal));
      document.add(new Paragraph("\n\n\n"));

      adicionarBlocoAssinatura(document, doc, fontBold, fontNormal);

      if (!DocumentoTipo.OFICIO.equals(doc.getTipo())) {
        adicionarCampoRecebimento(document, fontNormal);
      }

      document.close();
    } catch (Exception e) {
      System.err.println("Erro ao gerar PDF: " + e.getMessage());
    }

    return out.toByteArray();
  }

  private void montarLayoutOficio(Document document, Documento doc,
      Font fontBold, Font fontNormal) throws Exception {
    document.add(new Paragraph("OFÍCIO Nº " + doc.getNumero(), fontBold));

    DateTimeFormatter df = DateTimeFormatter.ofPattern(
        "'Pau dos Ferros/RN, 'dd 'de' MMMM 'de' yyyy",
        Locale.of("pt", "BR"));
    Paragraph pData = new Paragraph(doc.getData().format(df) + ".", fontNormal);
    pData.setAlignment(Element.ALIGN_RIGHT);
    document.add(pData);
    document.add(new Paragraph("\n"));

    document.add(new Paragraph("Ao " + doc.getInteressado() + ",", fontNormal));

    Paragraph pAssunto = new Paragraph("ASSUNTO: " + doc.getSubject(), fontBold);
    document.add(pAssunto);
    document.add(new Paragraph("\n"));
  }

  private void montarLayoutMemorando(Document document, Documento doc,
      Font fontBold, Font fontNormal) throws Exception {
    document.add(new Paragraph("MEMORANDO Nº " + doc.getNumero(), fontBold));
    document.add(new Paragraph("DESTINO: " + doc.getInteressado(), fontBold));

    DateTimeFormatter df = DateTimeFormatter.ofPattern(
        "dd 'de' MMMM 'de' yyyy", Locale.of("pt", "BR"));
    Paragraph pData = new Paragraph(doc.getData().format(df) + ".", fontNormal);
    pData.setAlignment(Element.ALIGN_RIGHT);
    document.add(pData);
    document.add(new Paragraph("\n"));

    document.add(new Paragraph("ASSUNTO: " + doc.getSubject(), fontBold));
    document.add(new Paragraph("\n"));
  }

  private void adicionarBackground(PdfWriter writer) throws Exception {
    Image background = Image.getInstance(
        new ClassPathResource("static/images/backgroundimage.jpeg").getURL());
    background.setAbsolutePosition(0, 0);
    background.scaleToFit(PageSize.A4.getWidth(), PageSize.A4.getHeight());
    writer.getDirectContentUnder().addImage(background);
  }

  private void adicionarBlocoAssinatura(Document document, Documento doc,
      Font fontBold, Font fontNormal) throws Exception {
    Paragraph pLinha = new Paragraph("___________________________________________", fontNormal);
    pLinha.setAlignment(Element.ALIGN_CENTER);
    document.add(pLinha);

    Paragraph pAssinante = new Paragraph(doc.getAssinante(), fontBold);
    pAssinante.setAlignment(Element.ALIGN_CENTER);
    document.add(pAssinante);

    String cargo = "Secretário Municipal de Infraestrutura – SEINFRA";
    Paragraph pCargo = new Paragraph(cargo, fontNormal);
    pCargo.setAlignment(Element.ALIGN_CENTER);
    document.add(pCargo);

    if (doc.getPortaria() != null) {
      Paragraph pPort = new Paragraph(doc.getPortaria(), fontNormal);
      pPort.setAlignment(Element.ALIGN_CENTER);
      document.add(pPort);
    }
  }

  private void adicionarCampoRecebimento(Document document, Font font) throws Exception {
    document.add(new Paragraph("\n\n\n\n\n"));
    PdfPTable table = new PdfPTable(2);
    table.setWidthPercentage(100);

    PdfPCell c1 = new PdfPCell(new Phrase("Recebido por:___________________________", font));
    c1.setBorder(Rectangle.NO_BORDER);
    table.addCell(c1);

    PdfPCell c2 = new PdfPCell(new Phrase("Em(data/horário):_____________________", font));
    c2.setBorder(Rectangle.NO_BORDER);
    c2.setHorizontalAlignment(Element.ALIGN_RIGHT);
    table.addCell(c2);

    document.add(table);
  }
}

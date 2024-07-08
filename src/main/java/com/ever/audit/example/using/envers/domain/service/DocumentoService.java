package com.ever.audit.example.using.envers.domain.service;

import com.ever.audit.example.using.envers.domain.exception.NegocioException;
import com.ever.audit.example.using.envers.domain.exception.ObjectNotFoundException;
import com.ever.audit.example.using.envers.domain.model.Documento;
import com.ever.audit.example.using.envers.domain.repository.DocumentoRepository;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.signatures.*;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;

import static com.ever.audit.example.using.envers.domain.service.Utils.Validations.verifyExtension;

@Service
@RequiredArgsConstructor
public class DocumentoService {

    private final DocumentoRepository documentoRepository;

    public Documento create (MultipartFile file, String assinatura) throws IOException {
        try {
            verifyExtension(file);
            byte[] fileBytes = file.getBytes();
            String base64Encoded = Base64.getEncoder().encodeToString(fileBytes);

            String docAssinado = assinarDoc(fileBytes,assinatura);
            String fileHash = generateFileHash(fileBytes);

            Documento newDocument = Documento.builder()
                    .nome(file.getOriginalFilename())
                    .fileBase64(docAssinado)
                    .fileHash(fileHash)
                    .build();
            return documentoRepository.save(newDocument);
        }catch (IOException e){
            throw new NegocioException(e.getMessage());
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    private X509Certificate loadCertificate() throws CertificateException {
        String aliasCert = "23177289894642709570741494420";
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        return (X509Certificate) certificateFactory.generateCertificate(new ByteArrayInputStream(Base64.getDecoder().decode(aliasCert)));
    }

    private String assinarDoc(byte[] fileBytes, String assinatura) throws IOException, GeneralSecurityException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfReader reader = new PdfReader(new ByteArrayInputStream(fileBytes));
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdfDoc = new PdfDocument(reader, writer);

        StampingProperties stampingProperties = new StampingProperties();

        PdfSigner signer = new PdfSigner(pdfDoc.getReader(), baos, stampingProperties);

        // Configurar a aparência da assinatura como invisível
        PdfSignatureAppearance appearance = signer.getSignatureAppearance();
        appearance.setReason("Razão da assinatura")
                .setLocation("Localização da assinatura")
                .setReuseAppearance(false);

        // Configurar o campo de assinatura invisível
        signer.setFieldName("InvisibleSignature");

        // Adicionar a assinatura ao campo
        signer.signExternalContainer(new IExternalSignatureContainer() {
            @Override
            public byte[] sign(InputStream inputStream) throws GeneralSecurityException {
                return Base64.getDecoder().decode(assinatura);
            }

            @Override
            public void modifySigningDictionary(PdfDictionary signDic) {
                signDic.put(PdfName.Filter, PdfName.Adobe_PPKLite);
                signDic.put(PdfName.SubFilter, PdfName.Adbe_pkcs7_detached);
            }
        }, 8192);

        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }

    public Documento findById(Long id) {
        Documento document = documentoRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("No document found with id : " + id));
        return document;
    }

    private String generateFileHash(byte[] fileBytes) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(fileBytes);
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }

    public byte[] getFileFromHash(String fileHash) throws IOException {
        byte[] fileBytes = Base64.getDecoder().decode(fileHash);
        byte[] signedFileBytes = addSignature(fileBytes);
        return signedFileBytes;
    }

    private String bytesToHex(byte[] hashBytes) {
        StringBuilder hexString = new StringBuilder(2 * hashBytes.length);
        for (byte b : hashBytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public byte[] addSignature(byte[] fileBytes) throws IOException {
        try (PDDocument document = PDDocument.load(new ByteArrayInputStream(fileBytes))) {
            PDPage page = document.getPage(0);

            PDRectangle mediaBox = page.getMediaBox();
            float pageWidth = mediaBox.getWidth();
            float pageHeight = mediaBox.getHeight();
            Rectangle2D signatureArea = new Rectangle2D.Double((pageWidth - 200) / 2, 720, 180, 50);

            if (isAreaEmpty(document,page, signatureArea)) {
                try (PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true)) {

//                    contentStream.beginText();
//                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
//                    contentStream.newLineAtOffset((float) signatureArea.getX(), (float) signatureArea.getY());
//                    contentStream.showText("Signature: John Doe");
//                    contentStream.endText();

                    PDImageXObject pdImage = PDImageXObject.createFromFile("static/signature1.jpeg", document);
                    contentStream.drawImage(pdImage, (float) signatureArea.getX(), (float) signatureArea.getY(),
                            (float) signatureArea.getWidth(), (float) signatureArea.getHeight());
                }

                ByteArrayOutputStream out = new ByteArrayOutputStream();
                document.save(out);
                return out.toByteArray();
            }
        else {
                throw new NegocioException("A área da assinatura não está vazia.");
            }
        }
    }

    private boolean isAreaEmpty(PDDocument document, PDPage page, Rectangle2D area) throws IOException {
        PDFTextStripperByArea stripper = new PDFTextStripperByArea();
        stripper.addRegion("signatureArea", area);
        stripper.extractRegions(page);

        String text = stripper.getTextForRegion("signatureArea");
        if (!text.trim().isEmpty()) {
            return false;
        }

        PDFRenderer pdfRenderer = new PDFRenderer(document);
        BufferedImage image = pdfRenderer.renderImageWithDPI(document.getPages().indexOf(page), 300, ImageType.RGB);
        int x = (int) area.getX();
        int y = (int) (image.getHeight() - area.getY() - area.getHeight());
        int width = (int) area.getWidth();
        int height = (int) area.getHeight();
        BufferedImage subImage = image.getSubimage(x, y, width, height);

        for (int i = 0; i < subImage.getWidth(); i++) {
            for (int j = 0; j < subImage.getHeight(); j++) {
                int pixel = subImage.getRGB(i, j);
                if ((pixel & 0xFFFFFF) != 0xFFFFFF) { // not white
                    return false;
                }
            }
        }

        return true;
    }

}
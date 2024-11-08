package yourstyle.com.shope.service;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;

import yourstyle.com.shope.model.Order;
import yourstyle.com.shope.model.OrderDetail;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Properties;

import org.springframework.stereotype.Service;

@Service
public class InvoiceService {

        @SuppressWarnings("resource")
        public ByteArrayOutputStream createInvoice(List<OrderDetail> orderDetails, Order order) {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                try (PdfWriter writer = new PdfWriter(outputStream)) {
                        PdfDocument pdf = new PdfDocument(writer);
                        Document document = new Document(pdf);
                        // Tạo font hỗ trợ tiếng Việt
                        PdfFont font;
                        try {
                                font = PdfFontFactory.createFont(
                                                getClass().getClassLoader().getResource("fonts/ARIAL.TTF").toString(),
                                                PdfEncodings.IDENTITY_H,
                                                PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);
                        } catch (IOException e) {
                                e.printStackTrace();
                                throw new RuntimeException("Could not create font.", e);
                        }
                        // Thêm logo và thông tin người gửi, người nhận
                        ImageData logoData = ImageDataFactory
                                        .create(getClass().getClassLoader().getResource("static/admin/img/logo.png"));
                        Image logo = new Image(logoData).scaleToFit(100, 50).setMarginRight(0);
                        ImageData ghnData = ImageDataFactory
                                        .create(getClass().getClassLoader()
                                                        .getResource("static/admin/img/LogoGHN.png"));
                        Image ghnLogo = new Image(ghnData).scaleToFit(100, 50).setMarginLeft(0);
                        // Tạo bảng header với 2 cột
                        Table headerTable = new Table(UnitValue.createPercentArray(new float[] { 1, 1 }));
                        headerTable.setWidth(UnitValue.createPercentValue(100));

                        // Tạo tên shop với font chữ màu xanh đậm và kích thước lớn
                        Text shopNameText = new Text("YourStyle")
                                        .setFontColor(new DeviceRgb(0, 102, 204)) // Màu xanh đậm
                                        .setFontSize(18) // Kích thước chữ lớn
                                        .setBold();
                        // Thêm tên shop vào cột thứ hai bên phải logo
                        Paragraph shopNameParagraph = new Paragraph(shopNameText)
                                        .setTextAlignment(TextAlignment.LEFT) // Căn chỉnh bên trái để gần logo
                                        .setVerticalAlignment(VerticalAlignment.MIDDLE) // Căn giữa theo chiều dọc
                                        .setMargin(0)// Loại bỏ margin và padding của Paragraph
                                        .setPadding(0);
                        // Tạo một Div chứa cả logo và tên shop
                        Div div = new Div().setVerticalAlignment(VerticalAlignment.MIDDLE);
                        div.add(logo);// logo website
                        div.add(shopNameParagraph);// tên YourStyle
                        Cell logoCell = new Cell().setPaddingRight(0).setPaddingLeft(0);
                        logoCell.add(div);
                        // logoCell.add(shopNameParagraph);
                        headerTable.addCell(logoCell);

                        headerTable.addCell(new Cell().add(ghnLogo).setPaddingLeft(0)); // logo GHN
                        // Thêm bảng header vào tài liệu
                        document.add(headerTable);

                        Table inforTable = new Table(UnitValue.createPercentArray(new float[] { 1, 1 }));
                        inforTable.setWidth(UnitValue.createPercentValue(100));
                        // Cột thông tin người gửi
                        Cell senderCell = new Cell();
                        senderCell.setPadding(10);
                        senderCell.setBorderTop(Border.NO_BORDER);
                        senderCell.add(new Paragraph("Từ:").setFont(font).setBold());
                        senderCell.add(new Paragraph("+ Shop thời trang YourStyle").setFont(font));
                        senderCell.add(new Paragraph("Đ/c: 116 Nguyễn Huy Tưởng, Hòa An, Liên Chiểu, Đà Nẵng ")
                                        .setFont(font));
                        senderCell.add(new Paragraph("SĐT: 0987676767").setFont(font));
                        inforTable.addCell(senderCell); // Thêm ô người gửi vào bảng

                        // Cột thông tin người nhận
                        Cell receiverCell = new Cell();
                        receiverCell.setPadding(10);
                        receiverCell.setBorderTop(Border.NO_BORDER);
                        receiverCell.add(new Paragraph("Đến:").setFont(font).setBold());
                        receiverCell.add(new Paragraph("+ " + order.getCustomer().getFullname()).setFont(font));

                        // Thêm thông tin địa chỉ người nhận
                        String recipientAddress = order.getCustomer().getAddresses().stream()
                                        .filter(address -> address.getIsDefault())
                                        .findFirst()
                                        .map(address -> address.getStreet() + ", " + address.getWard() + ", "
                                                        + address.getDistrict() + ", " + address.getCity())
                                        .orElse("Không có địa chỉ cụ thể");
                        receiverCell.add(new Paragraph("Đ/c: " + recipientAddress).setFont(font));
                        receiverCell.add(new Paragraph("SĐT: " + order.getCustomer().getPhoneNumber()).setFont(font));
                        inforTable.addCell(receiverCell); // Thêm ô người nhận vào bảng
                        // Thêm bảng vào tài liệu
                        document.add(inforTable);
                        // Tạo bảng sản phẩm với 2 cột (bên trái là QR code, bên phải là nội dung hàng)
                        Table productTable = new Table(UnitValue.createPercentArray(new float[] { 1, 5 }));
                        productTable.setWidth(UnitValue.createPercentValue(100));
                        // Thêm QR code
                        ImageData qrData = ImageDataFactory
                                        .create(getClass().getClassLoader().getResource("static/admin/img/QRcode.png"));
                        Image qrCode = new Image(qrData).scaleToFit(100, 100);
                        Cell qrCell = new Cell().add(qrCode);
                        qrCell.setBorderTop(Border.NO_BORDER);
                        qrCell.setPaddingTop(10);
                        productTable.addCell(qrCell);

                        Cell contentCell = new Cell();
                        // Nội dung đơn hàng
                        contentCell.add(new Paragraph("Nội dung hàng (Tổng SL sản phẩm: "
                                        + orderDetails.stream().mapToInt(OrderDetail::getQuantity).sum() + ")")
                                        .setFont(font)
                                        .setBold()); // tính tổng số lượng

                        // Tạo bảng chi tiết đơn hàng với 4 cột
                        Table detailTable = new Table(4);
                        detailTable.addHeaderCell(new Cell().add(new Paragraph("Tên sản phẩm").setFont(font)));
                        detailTable.addHeaderCell(new Cell().add(new Paragraph("Số lượng").setFont(font)));
                        detailTable.addHeaderCell(new Cell().add(new Paragraph("Giá").setFont(font)));
                        detailTable.addHeaderCell(new Cell().add(new Paragraph("Thành tiền").setFont(font)));

                        // Định dạng số
                        DecimalFormat formatter = new DecimalFormat("#,###");
                        // Thêm chi tiết sản phẩm vào bảng
                        for (OrderDetail detail : orderDetails) {
                                detailTable.addCell(detail.getProductVariant().getProduct().getName()).setFont(font);
                                detailTable.addCell(String.valueOf(detail.getQuantity()));
                                detailTable.addCell(formatter.format(detail.getPrice().setScale(0, RoundingMode.FLOOR))
                                                + ".000 VND");
                                detailTable.addCell(formatter.format(
                                                detail.getPrice().multiply(BigDecimal.valueOf(detail.getQuantity())))
                                                + ".000 VND");
                        }
                        contentCell.setBorderTop(Border.NO_BORDER);
                        contentCell.setPadding(10);
                        contentCell.add(detailTable);
                        productTable.addCell(contentCell);
                        // Thêm bảng sản phẩm vào tài liệu
                        document.add(productTable);

                        // Tạo bảng với 2 cột để chứa "Tiền thu người nhận" và "Chữ ký người nhận"
                        Table paymentTable = new Table(UnitValue.createPercentArray(new float[] { 1, 1 }));
                        paymentTable.setWidth(UnitValue.createPercentValue(100));
                        // Tạo ô cho "Tiền thu người nhận"
                        Cell paymentCell = new Cell().add(new Paragraph(
                                        "Tiền thu người nhận \n" + formatter.format(order.getTotalAmount())
                                                        + ".000 VND")
                                        .setFont(font).setBold());
                        paymentCell.setBorderRight(Border.NO_BORDER); // Bỏ viền
                        paymentCell.setBorderTop(Border.NO_BORDER);
                        paymentCell.setHeight(430); // Đặt chiều cao cho ô, có thể điều chỉnh theo ý muốn
                        paymentCell.setPadding(15);
                        paymentCell.setPaddingLeft(40);
                        paymentTable.addCell(paymentCell);

                        // Tạo bảng con bên trong ô chữ ký để chứa nội dung chữ ký và viền bao quanh
                        Table innerSignatureTable = new Table(UnitValue.createPercentArray(new float[] { 1 }));
                        innerSignatureTable.setWidth(UnitValue.createPercentValue(100));
                        innerSignatureTable.setBorder(new SolidBorder(1)); // Thêm viền bao quanh bảng con

                        // Tạo ô chứa nội dung chữ ký bên trong bảng con
                        Cell innerSignatureCell = new Cell().add(new Paragraph(
                                        "Chữ ký người nhận\t\t\n(Xác nhận hàng nguyên vẹn, không móp/méo)\t\t\t")
                                        .setFont(font)
                                        .setTextAlignment(TextAlignment.CENTER));
                        innerSignatureCell.setBorder(Border.NO_BORDER); // Bỏ viền bên trong của ô
                        innerSignatureCell.setHeight(140);
                        innerSignatureTable.addCell(innerSignatureCell);

                        // Thêm bảng con vào ô chữ ký
                        Cell signatureCell = new Cell().add(innerSignatureTable);
                        signatureCell.setBorderLeft(Border.NO_BORDER); // Bỏ viền trái của ô chữ ký
                        signatureCell.setBorderTop(Border.NO_BORDER);
                        signatureCell.setHeight(430); // Đặt chiều cao cho ô chữ ký
                        signatureCell.setPadding(10);
                        paymentTable.addCell(signatureCell);

                        // Thêm bảng vào tài liệu
                        document.add(paymentTable);
                        document.close();

                        // Ghi dữ liệu ra file tạm để kiểm tra
                        try (FileOutputStream fos = new FileOutputStream("test_invoice.pdf")) {
                                fos.write(outputStream.toByteArray());
                        } catch (IOException e) {
                                e.printStackTrace();
                        }
                } catch (Exception e) {
                        e.printStackTrace();
                }
                return outputStream;
        }

}

package yourstyle.com.shope.rest.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import yourstyle.com.shope.model.Order;
import yourstyle.com.shope.model.OrderDetail;
import yourstyle.com.shope.service.InvoiceService;
import yourstyle.com.shope.service.OrderDetailService;
import yourstyle.com.shope.service.OrderService;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.ResponseEntity;
import java.io.ByteArrayOutputStream;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceRestController {
    @Autowired
    OrderDetailService orderDetailService;
    @Autowired
    OrderService orderService;
    private final InvoiceService invoiceService;

    public InvoiceRestController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @GetMapping("/generate")
    public ResponseEntity<byte[]> generateInvoice(@RequestParam(name = "orderId") Integer orderId) {
        Order order = orderService.findById(orderId).orElse(null);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }

        List<OrderDetail> orderDetails = orderDetailService.findByOrder(order);
        ByteArrayOutputStream pdfStream = invoiceService.createInvoice(orderDetails, order);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.add("Content-Disposition", "inline; filename=invoice.pdf");

        return new ResponseEntity<>(pdfStream.toByteArray(), headers, HttpStatus.OK);
    }

    @GetMapping("/generate-instore")
    public ResponseEntity<byte[]> generateInvoice_InStore(@RequestParam(name = "orderId") Integer orderId) {
        Order order = orderService.findById(orderId).orElse(null);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }

        List<OrderDetail> orderDetails = orderDetailService.findByOrder(order);
        ByteArrayOutputStream pdfStream = invoiceService.createInvoice_InStore(orderDetails, order);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.add("Content-Disposition", "inline; filename=invoice_instore.pdf");

        return new ResponseEntity<>(pdfStream.toByteArray(), headers, HttpStatus.OK);
    }

}

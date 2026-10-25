package com.enterprise.app.business;

import com.enterprise.app.config.NotificationProperties;
import com.enterprise.app.log.TransactionLogContext;
import com.enterprise.app.model.dto.EmailAttachmentDto;
import com.enterprise.app.model.dto.EmailNotificationRequest;
import com.enterprise.app.model.dto.EmailRecipientDto;
import com.enterprise.app.model.dto.RecipientsDto;
import com.enterprise.app.model.enums.ContentType;
import com.enterprise.app.util.AttachmentDecoder;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class EmailNotificationService {
    private final Mailer mailer;
    private final NotificationProperties properties;

    @Inject
    public EmailNotificationService(Mailer mailer, NotificationProperties properties) {
        this.mailer = mailer;
        this.properties = properties;
    }

    public void send(EmailNotificationRequest request) {
        String transactionId = TransactionLogContext.getTransactionId();
        RecipientsDto recipients = request.getRecipients();
        List<String> to = addresses(recipients.getTo());
        List<String> cc = addresses(recipients.getCc());
        List<String> bcc = addresses(recipients.getBcc());
        Mail mail = new Mail()
                .setTo(to)
                .setCc(cc)
                .setBcc(bcc)
                .setSubject(request.getSubject());

        if (request.getContentType() == ContentType.HTML) {
            mail.setHtml(request.getContent());
        } else {
            mail.setText(request.getContent());
        }

        List<EmailAttachmentDto> attachments = request.getAttachments();
        if (attachments != null) {
            attachments.forEach(attachment -> mail.addAttachment(
                    attachment.getFileName(),
                    AttachmentDecoder.decode(
                            attachment.getContentBase64(),
                            properties.attachments().maxSizeBytes()),
                    attachment.getMediaType().getValue()));
        }

        mailer.send(mail);
        log.info("Correo enviado. transactionId={}, destinatarios={}, adjuntos={}",
                transactionId,
                to.size() + cc.size() + bcc.size(),
                attachments == null ? 0 : attachments.size());
    }

    private List<String> addresses(List<EmailRecipientDto> recipients) {
        return recipients == null
                ? List.of()
                : recipients.stream()
                        .filter(Objects::nonNull)
                        .map(EmailRecipientDto::getEmail)
                        .toList();
    }
}

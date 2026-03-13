package com.bingo.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendPasswordResetEmail(String to, String username, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            System.out.println(to);
            helper.setSubject("[Office Bingo] - Recuperação de Senha");

            String htmlMsg = """
                    <div style="background-color: #1e1e1e; color: #d4d4d4; font-family: 'Courier New', monospace; padding: 30px; border-radius: 8px; max-width: 600px; margin: 0 auto; border: 1px solid #333;">
                        <h2 style="color: #4ade80; margin-top: 0;">> ./recover_password.sh</h2>
                        <p>Olá <b>%s</b>,</p>
                        <p>Foi solicitada a redefinição de senha para a sua conta no Office Bingo.</p>
                        <p>O seu código de autorização (OTP) é:</p>
                        
                        <div style="background-color: #000000; padding: 20px; text-align: center; font-size: 32px; font-weight: bold; letter-spacing: 8px; color: #4ade80; border-radius: 4px; margin: 20px 0;">
                            %s
                        </div>
                        
                        <p style="color: #ef4444; font-size: 12px;">⚠️ Este código expira em 15 minutos.</p>
                        <p style="color: #888; font-size: 12px; margin-top: 30px;">Se você não solicitou esta alteração, ignore este e-mail. Nenhuma alteração será feita na sua conta.</p>
                        
                        <div style="border-top: 1px solid #333; margin-top: 20px; padding-top: 10px; font-size: 10px; color: #666;">
                            [SYSTEM_LOG] Request gerado automaticamente pelo servidor.
                        </div>
                    </div>
                    """.formatted(username, code);

            helper.setText(htmlMsg, true);
            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Erro ao enviar e-mail de recuperação", e);
        }
    }
}
package fr.auri.batch;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.support.RunIdIncrementer;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.DefaultCredentialsProvider;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import org.jasypt.util.text.BasicTextEncryptor;

import java.util.List;

/**
 * Created by CER3100444 on 06/06/2016.
 */
@Configuration
@EnableBatchProcessing
@EnableAutoConfiguration
public class BatchConfig {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
                .tasklet(new Tasklet() {
                    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
                        try {
                            testCineDay();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                })
                .build();
    }

    @Bean
    public Job job(Step step1) throws Exception {
        return jobBuilderFactory.get("job1")
                .incrementer(new RunIdIncrementer())
                .start(step1)
                .build();
    }


    public final void testEncryptMdp() throws Exception {
        BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
        textEncryptor.setPassword("toto");
        String mdpEncrypt=textEncryptor.encrypt(""); //Mets ton mot de passe Orange en clair :/
        System.out.println(mdpEncrypt);
    }

    private final void testCineDay() throws Exception {
        BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
        textEncryptor.setPassword("toto");

        try (final WebClient webClient = new WebClient(BrowserVersion.FIREFOX_38, "proxy1.cer31.recouv", 8000)) {

            // set proxy username and password
            final DefaultCredentialsProvider credentialsProvider = (DefaultCredentialsProvider) webClient.getCredentialsProvider();
            credentialsProvider.addCredentials("CER3100441", "Toulouse01");

            // 1. Authentification
            webClient.getOptions().setJavaScriptEnabled(false);
            final HtmlPage page1 = webClient.getPage("http://id.orange.fr/auth_user/bin/auth_user.cgi?cas=nowg&return_url=http%3A%2F%2Fmdsp.orange.fr%2Fcineday%2Fcommande.xhtml");
            final HtmlForm form = page1.getForms().get(0);
            HtmlSubmitInput submitButton = (HtmlSubmitInput) form.getElementsByAttribute("input", "type", "submit").get(0);
            final HtmlTextInput userId = form.getInputByName("credential");
            final HtmlPasswordInput passwd = form.getInputByName("password");
            // Change the value of the text field
            userId.setValueAttribute("dijoux.aurelien@orange.fr");
            passwd.setValueAttribute(textEncryptor.decrypt("VEVETSXHHtx05nB8dht19K6f68e+sYwZ")); //mot de passe crypté
            submitButton.click();

            // 2. Commande des codes
            webClient.getOptions().setJavaScriptEnabled(true);
            webClient.getOptions().setThrowExceptionOnScriptError(false);
            webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
            final HtmlPage pageCommandeCineDays = webClient.getPage("http://mdsp.orange.fr/cineday/commande.xhtml");

            HtmlPage pageCineDays = null;
            List<HtmlAnchor> anchors = pageCommandeCineDays.getAnchors();
            for (HtmlAnchor htmlAnchor : anchors) {
                if (htmlAnchor.getAttribute("id").contains("form_compte_0:j_idt")) {
                    htmlAnchor.click();
                    Thread.sleep(5000);
                    pageCineDays = (HtmlPage) webClient.getCurrentWindow().getEnclosedPage();
                    break;
                }
            }

            //3 .envoie du code par sms
            if (pageCineDays != null) {
                String codes = pageCineDays.asText().substring(pageCineDays.asText().indexOf("compte Internet+Mobile") + "compte Internet+Mobile".length(),
                        pageCineDays.asText().indexOf("› Envoyer ces codes par sms au"));

                final HtmlForm formEnvoieCode = pageCineDays.getForms().get(1);

                HtmlSubmitInput sendSms = (HtmlSubmitInput) formEnvoieCode
                        .getElementsByAttribute("input", "type", "submit").get(0);
                final HtmlTextInput telephone = formEnvoieCode.getInputByName("form_compte_0:input_phone_b_0");
                telephone.setValueAttribute("0604501870");
                sendSms.click();
            }
        }
    }

}


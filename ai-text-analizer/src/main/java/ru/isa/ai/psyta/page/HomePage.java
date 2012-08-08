package ru.isa.ai.psyta.page;

import com.google.common.util.concurrent.FutureCallback;
import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.form.upload.UploadProgressBar;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.file.Files;
import org.apache.wicket.util.file.Folder;
import org.apache.wicket.util.lang.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.isa.ai.linguistic.analyzers.SNCProgressor;
import ru.isa.ai.linguistic.analyzers.wordcount.WordCountAnalyzer;
import ru.isa.ai.linguistic.data.SNCPrimer;
import ru.isa.ai.linguistic.data.TxtFileDataLoader;
import ru.isa.ai.linguistic.utils.LinguisticUtils;
import ru.isa.ai.psyta.ACMApplication;
import ru.isa.ai.psyta.ACMSession;
import ru.isa.ai.psyta.page.lists.AbstractFileListContainer;
import ru.isa.ai.psyta.page.lists.WorkKeywordFilePanel;
import ru.isa.ai.psyta.page.lists.WorkTextFilePanel;

import java.io.File;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Homepage
 */
public class HomePage extends ACMPage {
    private static final Logger log = LoggerFactory.getLogger(HomePage.class);

    private static final String FILE_PART_DELIMITER = "\\s*\\n\\s*";

    private Label textHint;
    private FeedbackPanel uploadFeedback;
    private AnalizeResultsPanel resultsPanel;

    private Folder keywordsFolder;
    private Folder textsFolder;
    private Set<File> keywordFiles = new HashSet<>();
    private Map<String, Map<String, Integer>> analizeResults = Collections.synchronizedMap(new HashMap<String, Map<String, Integer>>());
    private Map<String, Integer> totalResults = Collections.synchronizedMap(new HashMap<String, Integer>());

    private class AnalyzeWorker extends Thread {

        private File file;
        private ACMSession session;

        public AnalyzeWorker(File file, ACMSession session) {
            this.file = file;
            this.session = session;
        }

        @Override
        public void run() {
            if (keywordFiles.size() > 0) {
                try {
                    session.setStatus(ACMSession.WorkStatus.WORKING);
                    session.setAnalyzingFile(file);

                    final long currentTime = System.currentTimeMillis();
                    // loading data
                    TxtFileDataLoader loader = new TxtFileDataLoader(file.getAbsolutePath(), "UTF-8");
                    loader.setDelimiter(FILE_PART_DELIMITER);
                    SNCPrimer primer = loader.loadData();

                    final Map<String, List<String>> keywordsByFile = new HashMap<String, List<String>>();
                    Set<String> allKeyWords = new HashSet<String>();

                    for (File keywordFile : keywordFiles) {
                        List<String> keywords = WordCountAnalyzer.loadKeywords(keywordFile);
                        allKeyWords.addAll(keywords);
                        keywordsByFile.put(keywordFile.getName(), keywords);
                    }

                    // analyzing
                    WordCountAnalyzer analyzer = new WordCountAnalyzer(allKeyWords);
                    analyzer.setProgressor(new SNCProgressor() {
                        @Override
                        public void markProgress(double part) {
                            AnalyzeWorker.this.session.setProgress(part);
                        }
                    });
                    analyzer.analyze(primer, new FutureCallback<Map<String, Integer>>() {
                        @Override
                        public void onSuccess(Map<String, Integer> results) {
                            // sorting
                            for (File keywordFile : keywordFiles) {
                                int commonFreq = 0;
                                Map<String, Integer> fileResults = new HashMap<String, Integer>();
                                for (Map.Entry<String, Integer> entry : results.entrySet()) {
                                    if (keywordsByFile.get(keywordFile.getName()).contains(entry.getKey())) {
                                        fileResults.put(entry.getKey(), entry.getValue());
                                        commonFreq += entry.getValue();
                                    }
                                }
                                HomePage.this.analizeResults.put(keywordFile.getName(), fileResults);
                                HomePage.this.totalResults.put(keywordFile.getName(), commonFreq);
                            }

                            session.info("Файл " + file.getName() + " проанализирован за " + LinguisticUtils.getFormattedIntervalFromCurrent(currentTime));
                            session.setStatus(ACMSession.WorkStatus.SUCCESS);
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            session.error("Ошибка в анализе файла: " + t.getMessage());
                            session.setStatus(ACMSession.WorkStatus.FAIL);
                        }
                    });


                } catch (Exception e) {
                    session.error("Ошибка при загрузке ключевых слов: " + e.getMessage());
                    session.setStatus(ACMSession.WorkStatus.FAIL);
                }
            } else {
                session.setStatus(ACMSession.WorkStatus.SUCCESS);
            }
        }
    }

    private enum UploadedFileType {
        TEXT("Файл для анализа"), KEYWORD_LIST("Файл со списком слов");

        private String text;

        UploadedFileType(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }
    }

    private class FileUploadForm extends Form<Void> {
        FileUploadField fileUploadField;
        DropDownChoice<UploadedFileType> typeSelector;

        /**
         * Construct.
         *
         * @param name Component name
         */
        public FileUploadForm(String name) {
            super(name);

            // set this form to multipart mode (allways needed for uploads!)
            setMultiPart(true);

            // Add one file input field
            add(fileUploadField = new FileUploadField("fileInput"));

            // Set maximum size to 100K for demo purposes
            setMaxSize(Bytes.megabytes(10));

            IChoiceRenderer<UploadedFileType> typeRenderer = new IChoiceRenderer<UploadedFileType>() {

                @Override
                public Object getDisplayValue(UploadedFileType object) {
                    return object.getText();
                }

                @Override
                public String getIdValue(UploadedFileType object, int index) {
                    return object.toString();
                }
            };
            typeSelector = new DropDownChoice<UploadedFileType>("typeFileSelector", new Model<UploadedFileType>(UploadedFileType.TEXT),
                    Arrays.asList(UploadedFileType.values()), typeRenderer);
            add(typeSelector);
        }

        /**
         * @see org.apache.wicket.markup.html.form.Form#onSubmit()
         */
        @Override
        protected void onSubmit() {
            final UploadedFileType selectedType = typeSelector.getModelObject();
            final List<FileUpload> uploads = fileUploadField.getFileUploads();
            if (uploads != null) {
                for (FileUpload upload : uploads) {
                    // Create a new file
                    File newFile = new File(selectedType == UploadedFileType.TEXT ? HomePage.this.textsFolder : HomePage.this.keywordsFolder,
                            upload.getClientFileName());

                    // Check new file, delete if it already existed
                    checkFileExists(newFile);
                    try {
                        // Save to new file
                        newFile.createNewFile();
                        upload.writeTo(newFile);

                        HomePage.this.info("файл успешно загружен: " + upload.getClientFileName());
                    } catch (Exception e) {
                        HomePage.this.error("Не удается загрузить файл: " + e.getMessage());
                    }
                }
            }
        }
    }

    private class TextFileListContainer extends AbstractFileListContainer {

        public TextFileListContainer(String id) {
            super(id, "Загруженные тексты", HomePage.this.textsFolder);
        }

        @Override
        protected Panel getWorkPanel(String id, File file) {
            return new WorkTextFilePanel(id, file) {
                protected void startWork(final AjaxRequestTarget target, final File file) {
                    ExecutorService executor = Executors.newSingleThreadExecutor();
                    executor.execute(new AnalyzeWorker(file, (ACMSession) getSession()));

                    info("Анализ файла " + file.getName() + " начался... ");
                    target.add(HomePage.this.uploadFeedback);
                    target.add(HomePage.this.textHint);
                }

                @Override
                public void markAnalyzingSuccess(final AjaxRequestTarget target) {
                    resultsPanel.refreshResults(target, HomePage.this.analizeResults, HomePage.this.totalResults);

                    target.add(HomePage.this.uploadFeedback);
                    target.add(HomePage.this.textHint);
                    target.add(HomePage.this.resultsPanel);
                }

                @Override
                public void markAnalyzingFail(final AjaxRequestTarget target) {
                    resultsPanel.setDefaultView(target);

                    target.add(HomePage.this.uploadFeedback);
                    target.add(HomePage.this.textHint);
                    target.add(HomePage.this.resultsPanel);
                }

                @Override
                public Component getTrobbler() {
                    return HomePage.this.resultsPanel.getTrobbler();
                }
            };
        }

        @Override
        public void afterFileDeleting(final AjaxRequestTarget target) {
            target.add(HomePage.this.uploadFeedback);
            target.add(HomePage.this.textHint);
        }
    }

    private class KeywordFileListContainer extends AbstractFileListContainer {

        private KeywordFileListContainer(String id) {
            super(id, "Загруженные списки слов", HomePage.this.keywordsFolder);
        }

        @Override
        protected Panel getWorkPanel(String id, File file) {
            return new WorkKeywordFilePanel(id, file) {
                protected void doWork(final AjaxRequestTarget target, final File file) {
                    if (isUsing(file)) {
                        HomePage.this.keywordFiles.remove(file);
                        HomePage.this.analizeResults.remove(file.getName());
                        HomePage.this.totalResults.remove(file.getName());
                    } else {
                        HomePage.this.keywordFiles.add(file);
                    }
                }

                @Override
                public Component getTrobbler() {
                    return HomePage.this.resultsPanel.getTrobbler();
                }

                @Override
                public boolean isUsing(final File file) {
                    return HomePage.this.keywordFiles.contains(file);
                }
            };
        }

        @Override
        public void afterFileDeleting(final AjaxRequestTarget target) {
            target.add(HomePage.this.uploadFeedback);
            target.add(HomePage.this.textHint);
        }
    }

    public HomePage() {
        keywordsFolder = new Folder(getUploadFolder(), "keywords");
        keywordsFolder.mkdir();
        textsFolder = new Folder(getUploadFolder(), "texts");
        textsFolder.mkdir();

        init();
    }

    private void init() {
        final FileUploadForm progressUploadForm = new FileUploadForm("fileUploadForm");
        progressUploadForm.add(new UploadProgressBar("progress", progressUploadForm, progressUploadForm.fileUploadField));
        add(progressUploadForm);

        WebMarkupContainer container = new WebMarkupContainer("emptyItems");
        uploadFeedback = new FeedbackPanel("uploadFeedback");
        uploadFeedback.setOutputMarkupPlaceholderTag(true);
        textHint = new Label("textHint", "Информация о действиях") {
            public boolean isVisible() {
                return getSession().getFeedbackMessages().size() == 0;
            }
        };
        textHint.setOutputMarkupPlaceholderTag(true);
        container.add(uploadFeedback);
        container.add(textHint);
        add(container);

        add(new TextFileListContainer("textFilesContainer"));
        add(new KeywordFileListContainer("keywordFilesContainer"));

        resultsPanel = new AnalizeResultsPanel("analizeResultsPanel", analizeResults, totalResults);
        resultsPanel.setOutputMarkupPlaceholderTag(true);
        add(resultsPanel);
    }

    private void checkFileExists(File newFile) {
        if (newFile.exists()) {
            if (!Files.remove(newFile)) {
                throw new IllegalStateException("Unable to overwrite " + newFile.getAbsolutePath());
            }
        }
    }

    private Folder getUploadFolder() {
        return ((ACMApplication) Application.get()).getUploadFolder();
    }
}

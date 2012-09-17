package ru.isa.ai.psyta.page;

import org.apache.wicket.Component;
import org.apache.wicket.IClusterable;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.markup.html.link.DownloadLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.http.WebResponse;
import ru.isa.ai.psyta.ACMApplication;
import ru.isa.ai.psyta.ACMSession;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * User: GraffT
 * Date: 21.10.11
 * Time: 11:25
 */
public class AnalizeResultsPanel extends Panel {

    private static final SimpleDateFormat FILE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private class ResultWordBean implements IClusterable {
        private String word;
        private Integer freq;

        private ResultWordBean() {
        }

        private ResultWordBean(String word, Integer freq) {
            this.word = word;
            this.freq = freq;
        }

        public String getWord() {
            return word;
        }

        public void setWord(String word) {
            this.word = word;
        }

        public Integer getFreq() {
            return freq;
        }

        public void setFreq(Integer freq) {
            this.freq = freq;
        }
    }

    private class ResultFileRowBean implements IClusterable {
        private String fileName1;
        private Integer totalFreq1;
        private List<ResultWordBean> beans1;

        private String fileName2;
        private Integer totalFreq2;
        private List<ResultWordBean> beans2;

        private ResultFileRowBean() {
        }

        public String getFileName1() {
            return fileName1;
        }

        public void setFileName1(String fileName1) {
            this.fileName1 = fileName1;
        }

        public Integer getTotalFreq1() {
            return totalFreq1;
        }

        public void setTotalFreq1(Integer totalFreq1) {
            this.totalFreq1 = totalFreq1;
        }

        public List<ResultWordBean> getBeans1() {
            return beans1;
        }

        public void setBeans1(List<ResultWordBean> beans1) {
            this.beans1 = beans1;
        }

        public String getFileName2() {
            return fileName2;
        }

        public void setFileName2(String fileName2) {
            this.fileName2 = fileName2;
        }

        public Integer getTotalFreq2() {
            return totalFreq2;
        }

        public void setTotalFreq2(Integer totalFreq2) {
            this.totalFreq2 = totalFreq2;
        }

        public List<ResultWordBean> getBeans2() {
            return beans2;
        }

        public void setBeans2(List<ResultWordBean> beans2) {
            this.beans2 = beans2;
        }
    }

    private List<ResultFileRowBean> resultList = new ArrayList<ResultFileRowBean>();

    private final WebMarkupContainer showItems;
    private final WebMarkupContainer emptyItems;
    private final DownloadLink downloadLink;
    private final ContextImage searchLabel;
    private int totalCount = 0;

    public AnalizeResultsPanel(String id, final Map<String, Map<String, Integer>> analizeResults, final Map<String, Integer> totalResults) {
        super(id);
        refreshBeans(analizeResults, totalResults);

        showItems = new WebMarkupContainer("showItems");
        showItems.setOutputMarkupPlaceholderTag(true);
        showItems.add(new PropertyListView<ResultFileRowBean>("item", resultList) {

            @Override
            protected void populateItem(ListItem<ResultFileRowBean> resultFileBeanListItem) {
                final ResultFileRowBean bean = resultFileBeanListItem.getModelObject();

                resultFileBeanListItem.add(new Label("fileName1"));
                resultFileBeanListItem.add(new Label("totalFreq1"));
                resultFileBeanListItem.add(new Label("percentFreq1", String.format("%.1f", 100*(bean.getTotalFreq1() + 0.0) / totalCount)));
                resultFileBeanListItem.add(new PropertyListView<ResultWordBean>("beans1") {

                    @Override
                    protected void populateItem(ListItem<ResultWordBean> resultWordBeanListItem) {
                        final ResultWordBean wordBean = resultWordBeanListItem.getModelObject();

                        resultWordBeanListItem.add(new Label("word"));
                        resultWordBeanListItem.add(new Label("freq"));
                    }
                });

                resultFileBeanListItem.add(new Label("fileName2") {
                    @Override
                    public boolean isVisible() {
                        return bean.getFileName2() != null;
                    }
                });
                resultFileBeanListItem.add(new Label("totalFreq2"));
                resultFileBeanListItem.add(new Label("percentFreq2", bean.getTotalFreq2() != null ? String.format("%.1f", 100*(bean.getTotalFreq2() + 0.0) / totalCount) : ""));
                resultFileBeanListItem.add(new PropertyListView<ResultWordBean>("beans2") {

                    @Override
                    protected void populateItem(ListItem<ResultWordBean> resultWordBeanListItem) {
                        final ResultWordBean wordBean = resultWordBeanListItem.getModelObject();

                        resultWordBeanListItem.add(new Label("word"));
                        resultWordBeanListItem.add(new Label("freq"));
                    }
                });
            }
        });

        downloadLink = new DownloadLink("saveLink", new Model<File>() {
            public File getObject() {
                File file = new File(((ACMApplication) getApplication()).getResultFolder(), "results_" +
                        FILE_DATE_FORMAT.format(new Date()) + "_" + Math.round(Math.random() * 9999) + ".txt");
                FileWriter writer = null;
                try {
                    writer = new FileWriter(file);
                    writeResultFile(writer);
                } catch (Exception ignored) {
                } finally {
                    if (writer != null) {
                        try {
                            writer.close();
                        } catch (IOException ignored) {

                        }
                    }
                }
                return file;
            }
        }) {

            @Override
            public void onClick() {
                WebResponse response = (WebResponse) getRequestCycle().getResponse();
                response.disableCaching();
                super.onClick();
            }

            @Override
            protected void onComponentTag(final ComponentTag tag) {
                super.onComponentTag(tag);

                String url = tag.getAttributes().getString("href");
                url = url + (url.contains("?") ? "&" : "?");
                url = url + "antiCache=" + System.currentTimeMillis();

                tag.put("href", url);
            }

        }.setDeleteAfterDownload(true);
        downloadLink.setOutputMarkupPlaceholderTag(true);
        downloadLink.setVisible(false);
        showItems.add(downloadLink);

        emptyItems = new WebMarkupContainer("emptyItems");
        emptyItems.setOutputMarkupPlaceholderTag(true);
        add(showItems);
        add(emptyItems);

        searchLabel = new ContextImage("throbblerIcon", "/_pics/throbbler.gif");
        searchLabel.setOutputMarkupPlaceholderTag(true);
        searchLabel.setVisible(((ACMSession) getSession()).getStatus() == ACMSession.WorkStatus.WORKING);
        add(searchLabel);
    }

    public void refreshResults(AjaxRequestTarget target, Map<String, Map<String, Integer>> analizeResults, Map<String, Integer> totalResults) {
        refreshBeans(analizeResults, totalResults);

        if (totalResults.size() > 0) {
            emptyItems.setVisible(false);
            downloadLink.setVisible(true);
        } else {
            emptyItems.setVisible(true);
            downloadLink.setVisible(false);
        }

        target.add(downloadLink);
        target.add(showItems);
        target.add(emptyItems);
    }

    public void setDefaultView(AjaxRequestTarget target) {
        refreshBeans(Collections.<String, Map<String, Integer>>emptyMap(), Collections.<String, Integer>emptyMap());

        emptyItems.setVisible(true);
        target.add(showItems);
        target.add(emptyItems);
    }

    private void refreshBeans(Map<String, Map<String, Integer>> analizeResults, Map<String, Integer> totalResults) {
        resultList.clear();
        ResultFileRowBean currentBean = new ResultFileRowBean();
        for (Map.Entry<String, Integer> entry : totalResults.entrySet()) {
            totalCount += entry.getValue();
            List<ResultWordBean> wordList = new ArrayList<ResultWordBean>();
            for (Map.Entry<String, Integer> wordEntry : analizeResults.get(entry.getKey()).entrySet()) {
                wordList.add(new ResultWordBean(wordEntry.getKey(), wordEntry.getValue()));
            }
            Collections.sort(wordList, new Comparator<ResultWordBean>() {
                @Override
                public int compare(ResultWordBean o1, ResultWordBean o2) {
                    return -o1.getFreq().compareTo(o2.getFreq());
                }
            });
            if (currentBean.getFileName1() == null) {
                currentBean.setFileName1(entry.getKey());
                currentBean.setTotalFreq1(entry.getValue());
                currentBean.setBeans1(wordList);
            } else {
                currentBean.setFileName2(entry.getKey());
                currentBean.setTotalFreq2(entry.getValue());
                currentBean.setBeans2(wordList);
                resultList.add(currentBean);
                currentBean = new ResultFileRowBean();
            }
        }
        if (currentBean.getBeans1() != null) {
            resultList.add(currentBean);
        }
    }

    public Component getTrobbler() {
        return searchLabel;
    }

    protected void writeResultFile(FileWriter writer) {
        for (ResultFileRowBean entry : resultList) {
            try {
                writer.write(String.format("%s - %d\n", entry.getFileName1(), entry.getTotalFreq1()));
                for (ResultWordBean wordEntry : entry.getBeans1()) {
                    writer.write(String.format("   %s - %d\n", wordEntry.getWord(), wordEntry.getFreq()));
                }
                writer.write("\n\n-----------------\n\n");
                if (entry.getFileName2() != null) {
                    writer.write(String.format("%s - %d\n", entry.getFileName2(), entry.getTotalFreq2()));
                    for (ResultWordBean wordEntry : entry.getBeans2()) {
                        writer.write(String.format("   %s - %d\n", wordEntry.getWord(), wordEntry.getFreq()));
                    }
                }
                writer.write("\n\n-----------------\n\n");
            } catch (IOException ignored) {

            }
        }
    }
}

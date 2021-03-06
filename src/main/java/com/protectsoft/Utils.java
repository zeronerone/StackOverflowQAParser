package com.protectsoft;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public  class Utils {
	
	//prevent default initialization
	private Utils() {}
	
	private static Logger log = Logger.getLogger(Utils.class.getName());
		
	private static volatile Utils singleton;
	
	
	public static Utils getSingleton() {
		if(singleton == null) {
			synchronized(Utils.class) {
				if(singleton == null) {
					singleton = new Utils();
				}
			}
		}
		return singleton;
	}
	
	
	
	public   List<StackQuestionModel> getQuestions(String url) throws IOException {
		
        List<StackQuestionModel> questions = new ArrayList<StackQuestionModel>();

        Document doc = Jsoup.connect(url).data("query", "Java")
        		  .userAgent("Mozilla")
        		  .cookie("auth", "token")
        		  .timeout(3000)
        		  .post();
       
        Elements elements = doc.select("div[data-position]");

        for(Element e : elements) {

            StackQuestionModel stackAnswer = new StackQuestionModel();

            Element q =  e.select("div.status.answered-accepted").first();
            
            if(q != null) {
            	
                try {
                    int num = Integer.parseInt(q.getElementsByTag("strong").text());
                    stackAnswer.setNumOfAnswers(num);

                    if(num > 0) {
                        q = e.select("div.result-link").first();

                        String title = q.select("a").attr("title");
                        String link = q.select("a").attr("href");

                        stackAnswer.setUrl(link);
                        stackAnswer.setTitle(title);
                       
                        questions.add(stackAnswer);
                    }

                } catch (Exception ex) {
                }


            } else {

                q = e.select("div.status.answered").first();
                if(q != null) {

                    try {
                        int num = Integer.parseInt(q.getElementsByTag("strong").text());
                        stackAnswer.setNumOfAnswers(num);

                        if(num > 0) {
                            q = e.select("div.result-link").first();

                            String title = q.select("a").attr("title");
                            String link = q.select("a").attr("href");

                            stackAnswer.setUrl(link);
                            stackAnswer.setTitle(title);

                            questions.add(stackAnswer);
                        }

                    } catch (Exception ex) {
                    }
                }

            }

        }
        return questions;
    }



    public <T> StackAnswerModel  getAnswerForQuestion(T q) throws IOException {
    
    	final String url = ((StackQuestionModel) q).getUrl();

        StackAnswerModel stackAnswerModel = new StackAnswerModel();

        Document doc = Jsoup.connect(url).data("query", "Java")
      		  .userAgent("Mozilla")
      		  .cookie("auth", "token")
      		  .timeout(3000)
      		  .post();

        Element element = doc.select("div.question").first();
        Element element1 = element.select("div.post-text").first();

        Elements paragraphs = element1.select("p");
        Elements codes = element1.select("code");

        stackAnswerModel.setUrl(((StackQuestionModel) q).getUrl());
        stackAnswerModel.setTitle(((StackQuestionModel) q).getTitle());

        String paragraphText = "";
        for(Element e : paragraphs) {
            paragraphText += e.text() + "\n";
        }

        String codetext = "";
        for(Element e : codes) {
            codetext += e.text() + "\n";
        }

        StackAnswerModel.QuestionText questionText = new StackAnswerModel.QuestionText();

        questionText.setCode(codetext);

        questionText.setText(paragraphText);
        stackAnswerModel.setQuestionText(questionText);

        //-----

        List<StackAnswerModel.AnswerText> answerTexts = new ArrayList<StackAnswerModel.AnswerText>();

        try {

            for (int i = 0; i < ((StackQuestionModel) q).getNumOfAnswers(); i++) {

                Element element2 = doc.select("div.answer").get(i);
                Element element3 = element2.select("div.post-text").first();

                paragraphs = element3.select("p");
                codes = element3.select("code");

                paragraphText = "";
                for (Element e : paragraphs) {
                    paragraphText += e.text() + "\n";
                }

                codetext = "";
                for (Element e : codes) {
                    codetext += e.text() + "\n";
                }

                StackAnswerModel.AnswerText answerText = new StackAnswerModel.AnswerText();
                answerText.setCode(codetext);
                answerText.setText(paragraphText);
                answerText.setUrl(((StackQuestionModel) q).getUrl());

                answerTexts.add(answerText);
            }

        } catch (IndexOutOfBoundsException ex) {

        }

        stackAnswerModel.setCodeAnswers(answerTexts);

        return stackAnswerModel;

    }



    public static String questionBuilder(String s) {
        String res = s.replace(" ","+");
        return res;
    }
    
    

}

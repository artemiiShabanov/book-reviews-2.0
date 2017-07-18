package util;

import Exceptions.DriverWasClosedException;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;
import Exceptions.BooksNotFoundException;
import model.Book;
import model.Review;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Supporting functions for working with selenium web driver.
 */
public class WebDriverUtil {

    private static WebDriver driver;
    static{
        File file = new File("C:/Selenium/chromedriver.exe");
        System.setProperty("webdriver.chrome.driver", file.getAbsolutePath());
        driver = new ChromeDriver();
    }

    //Google

    /**
     * Searching for books on google books.
     * Using findBooksOzon() on fail.
     * @param title title.
     * @param author author.
     * @return set of books.
     * @throws BooksNotFoundException
     */
    public static HashSet<Book> findBooksGoogle(String title, String author) throws BooksNotFoundException, DriverWasClosedException {
        try {

            HashSet<Book> result = new HashSet<>();

            //Data entry for search
            driver.get("https://books.google.ru/advanced_book_search?hl=ru");
            WebElement titleField = driver.findElement(By.name("as_vt"));
            titleField.sendKeys(title);
            WebElement authorField = driver.findElement(By.name("as_auth"));
            authorField.sendKeys(author);
            new Select(driver.findElement(By.name("num"))).selectByIndex(4);

            driver.findElement(By.name("btnG")).click();

            try {
                //if we get error
                driver.findElement(By.id("main-frame-error"));
                return findBooksOzon(title, author);
            } catch (NoSuchElementException e) {
                try {
                    //if we get capcha
                    driver.findElement(By.id("recaptcha"));
                    return findBooksOzon(title, author);
                } catch (NoSuchElementException e1) {
                    try {
                        //is there are no such books
                        driver.findElement(By.className("mnr-c"));
                        return findBooksOzon(title, author);
                    } catch (NoSuchElementException e2) {
                        List<WebElement> list = driver.findElements(By.className("rc"));
                        for (WebElement we : list) {
                            Book b = createBookFromWEGoogle(we);
                            if (b != null) {
                                result.add(b);
                            }
                        }
                    }
                }
            }

            return result;


        }
        catch(WebDriverException e) {
            driver = new ChromeDriver();
            throw new DriverWasClosedException();
        }

    }

    /**
     * Supporting function to build book from google books.
     * @param we div containing information about a book.
     * @return book.
     */
    private static Book createBookFromWEGoogle(WebElement we) {
        String tmpTitle;
        String tmpAuthor;
        try {
            tmpTitle = we.findElement(By.cssSelector(".r a")).getText();
        }
        catch(Exception e) {
            return null;
        }
        try {
            tmpAuthor = we.findElement(By.className("fl")).getText();
        }
        catch(Exception e) {
            tmpAuthor = "";
        }
        if (tmpAuthor == "Перевести эту страницу") {
            tmpAuthor = "";
        }
        return new Book(tmpTitle, tmpAuthor);
    }

    //Ozon.ru

    /**
     * Loading reviews from ozon.ru.
     * @param book what we are searching reviews for.
     * @return list of reviews(size = 0 if there are no reviews).
     */
    public static ArrayList<Review> loadReviewsOzon(Book book) throws DriverWasClosedException {
        try {

            ArrayList<Review> result = new ArrayList<>();

            //Date entry for search
            driver.get("http://www.ozon.ru/context/div_book/");
            WebElement searchField = driver.findElement(By.name("SearchText"));
            searchField.sendKeys(book.getTitle() + " " + book.getAuthor());
            driver.findElement(By.className("eMainSearchBlock_ButtonWrap")).click();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                //NOP
            }

            try {
                driver.findElement(By.className("eZeroSearch_Top"));
                return result;
            } catch (NoSuchElementException ex) {
                try {
                    Thread.sleep(500);
                    driver.get(driver.findElement(By.cssSelector(".eOneTile_image_link.jsUpdateLink.jsPic")).getAttribute("href"));
                } catch (NoSuchElementException r) {
                    //NOP
                } catch (InterruptedException e) {
                    //NOP
                }
                try {
                    JavascriptExecutor js = ((JavascriptExecutor) driver);
                    js.executeScript("window.scrollTo(0,document.body.scrollHeight);");
                    Thread.sleep(500);
                    driver.get(driver.findElement(By.cssSelector("a.eCommentsHeader_Title_Link")).getAttribute("href"));

                    List<WebElement> list = driver.findElements(By.cssSelector(".bComment.jsComment"));
                    for (WebElement we : list) {
                        result.add(new Review(we.findElement(By.className("eComment_Text_Text")).getText(), we.findElement(By.className("eComment_Info_Username_Link")).getText(), "ozon.ru", DateUtil.parseOzon(we.findElement(By.className("eComment_Info_Date")).getText())));
                    }
                } catch (NoSuchElementException e) {
                    return result;
                } catch (InterruptedException e) {
                    //NOP
                }

            }
            return result;

        }
        catch(WebDriverException e) {
            driver = new ChromeDriver();
            throw new DriverWasClosedException();
        }
    }

    /**
     * Searching for books on ozon.ru
     * @param title book title.
     * @param author book author.
     * @return set of books for this arguments.
     */
    public static HashSet<Book> findBooksOzon(String title, String author) throws BooksNotFoundException, DriverWasClosedException {
        try {

            HashSet<Book> result = new HashSet<>();

            //Data entry for search
            driver.get("http://www.ozon.ru/context/div_book/");
            WebElement searchField = driver.findElement(By.name("SearchText"));
            searchField.sendKeys(title + " " + author);
            driver.findElement(By.className("eMainSearchBlock_ButtonWrap")).click();

            try {
                //if there are no such books
                driver.findElement(By.className("eZeroSearch_Top"));
                throw new BooksNotFoundException("There are no such books.");
            } catch (NoSuchElementException e) {
                scrollDown(driver);
                List<WebElement> list = driver.findElements(By.cssSelector(".bOneTile.inline.jsUpdateLink"));
                Book b = null;
                for (WebElement we : list) {
                    b = createBookFromWEOzon(we);

                    if (b != null) {
                        result.add(b);
                    }
                }
            }

            result.remove(new Book("", ""));
            return result;

        }
        catch(WebDriverException e) {
            driver = new ChromeDriver();
            throw new DriverWasClosedException();
        }
    }

    /**
     * Supporting function to scroll down ang get all the books on ozon.ru.
     */
    private static void scrollDown(WebDriver driver) throws DriverWasClosedException {
        try {

            try {
                List<WebElement> list;
                JavascriptExecutor js = ((JavascriptExecutor) driver);
                for (int i = 0; i < 3; i++) {
                    js.executeScript("window.scrollTo(0,document.body.scrollHeight);");
                    Thread.sleep(150);
                }

                int total = Integer.parseInt(driver.findElement(By.className("eTileSeparator_Total")).getText());
                list = driver.findElements(By.cssSelector("div span.eTileSeparator_Text:last-child"));
                while (Integer.parseInt(list.get(list.size() - 1).getText().split(" ")[1]) != total) {
                    list = driver.findElements(By.cssSelector("div span.eTileSeparator_Text:last-child"));
                    js.executeScript("window.scrollTo(0,document.body.scrollHeight);");
                    Thread.sleep(150);
                }
            } catch (Exception e) {
                //NOP
            }

        }
        catch(WebDriverException e) {
            driver = new ChromeDriver();
            throw new DriverWasClosedException();
        }
    }

    /**
     * Supporting function to build book from ozon.
     * @param we div with book info.
     * @return book.
     */
    private static Book createBookFromWEOzon(WebElement we){
        String tmpTitle;
        String tmpAuthor;
        try {
            tmpTitle = we.findElement(By.cssSelector(".eOneTile_ItemName")).getText();
        }
        catch(NoSuchElementException e) {
            return null;
        }
        try {
            tmpAuthor = we.findElement(By.cssSelector(".bOneTileProperty.mPerson")).getText();
        }
        catch(NoSuchElementException e) {
            tmpAuthor = "";
        }
        return new Book(tmpTitle, tmpAuthor);

    }

    //Labirint

    /**
     * Loading reviews from labirint.ru.
     * @param book what we are searching reviews for.
     * @return list of reviews(size = 0 if there are no reviews).
     */
    public static ArrayList<Review> loadReviewsLabirint(Book book) throws DriverWasClosedException {
        try {

            ArrayList<Review> result = new ArrayList<>();

            driver.get("https://www.labirint.ru/");
            WebElement searchField = driver.findElement(By.className("search-top-input"));
            searchField.sendKeys(book.getTitle() + " " + book.getAuthor());
            driver.findElement(By.className("search-top-submit")).click();


            try {
                driver.findElement(By.className("empty-result"));
                return result;
            } catch (NoSuchElementException ex) {
                try {
                    driver.get(driver.findElement(By.className("cover")).getAttribute("href"));
                } catch (NoSuchElementException r) {
                    //NOP
                }
                try {
                    driver.get(driver.findElement(By.cssSelector("#product-comments-title a")).getAttribute("href"));

                    List<WebElement> listAuthor = driver.findElements(By.cssSelector(".user-name a"));
                    List<WebElement> listText = driver.findElements(By.cssSelector(".comment-text.content-comments p"));
                    List<WebElement> listDate = driver.findElements(By.cssSelector(".comment-footer .date"));

                    for (int i = 0; i < listAuthor.size(); i++) {
                        result.add(new Review(listText.get(i).getText(), listAuthor.get(i).getText(), "labirint.ru", DateUtil.parseLabirint(listDate.get(i).getText())));
                    }

                } catch (NoSuchElementException e) {
                    return result;
                }
            }
            return result;

        }
        catch(WebDriverException e) {
            driver = new ChromeDriver();
            throw new DriverWasClosedException();
        }
    }

    //Other stuff

    /**
     * Closing driver. Running by MainApp.
     */
    public static void finish() {
        driver.close();
        driver.quit();
    }
}

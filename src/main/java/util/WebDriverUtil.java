package util;

import Exceptions.DriverWasClosedException;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import Exceptions.BooksNotFoundException;
import model.Book;
import model.Review;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Supporting functions for working with selenium web driver.
 */
public class WebDriverUtil {

    private static WebDriver driver;
    static{
        URL url = Resources.getResource("ChromeWDHome");
        try {
            String chromeHome = Resources.toString(url, Charsets.UTF_8);
            System.setProperty("webdriver.chrome.driver", chromeHome);
            driver = new ChromeDriver();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            new Select(driver.findElement(By.name("num"))).selectByIndex(2);

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
            finish();
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

            WebDriverWait wait = new WebDriverWait(driver, 10);

            try {
                driver.findElement(By.className("eZeroSearch_Top"));
                return result;
            } catch (NoSuchElementException ex) {
                try {
                    driver.findElement(By.cssSelector(".eOneTile_tileLink.jsUpdateLink"));
                    driver.get(driver.findElements(By.cssSelector(".eOneTile_tileLink.jsUpdateLink")).get(1).getAttribute("href"));
                } catch (NoSuchElementException r) {
                    //there are only 1 book for this request
                    //NOP
                }
                try {
                    ((JavascriptExecutor) driver).executeScript("window.scrollTo(0,document.body.scrollHeight);");
                    wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("a.eCommentsHeader_Title_Link")));
                    driver.get(driver.findElement(By.cssSelector("a.eCommentsHeader_Title_Link")).getAttribute("href"));

                    List<WebElement> list = driver.findElements(By.cssSelector(".bComment.jsComment"));
                    for (WebElement we : list) {
                        result.add(new Review(we.findElement(By.className("eComment_Text_Text")).getText(), we.findElement(By.className("eComment_Info_Username_Link")).getText(), "ozon.ru", DateUtil.parseOzon(we.findElement(By.className("eComment_Info_Date")).getText()), getMarkFromWEOzon(we)));
                    }
                } catch (NoSuchElementException e) {
                    return result;
                }

            }
            return result;

        }
        catch(WebDriverException e) {
            finish();
            driver = new ChromeDriver();
            throw new DriverWasClosedException();
        }
    }

    /**
     * Supporting function to get mark on ozon.ru.
     * @param we
     * @return mark
     */
    private static int getMarkFromWEOzon(WebElement we) {
        we = we.findElement(By.cssSelector(".bStars.inline"));
        String c = we.getAttribute("class").split(" ")[2];
        return (c.charAt(1) - '0')*2;
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
                ((JavascriptExecutor) driver).executeScript("window.scrollTo(0,document.body.scrollHeight);");
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
            finish();
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
                    List<WebElement> listMark = driver.findElements(By.id("mark-stars"));


                    for (int i = 0; i < listMark.size(); i++) {
                        result.add(new Review(listText.get(i).getText(), listAuthor.get(i).getText(), "labirint.ru", DateUtil.parseLabirint(listDate.get(i).getText()), getMarkFromWELabirint(listMark.get(i))));
                    }

                } catch (NoSuchElementException e) {
                    return result;
                }
            }
            return result;

        }
        catch(WebDriverException e) {
            finish();
            driver = new ChromeDriver();
            throw new DriverWasClosedException();
        }
    }

    /**
     * Supporting function to get mark on labirint.ru.
     * @param we
     * @return mark
     */
    private static int getMarkFromWELabirint(WebElement we) {
        int result = 0;
        for( WebElement el: we.findElements(By.cssSelector(".star"))) {
            if (el.getAttribute("class").split(" ")[1].equals("full")) {
                result++;
            }
        }
        return result;
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

import java.io.IOException;
import java.net.URISyntaxException;

import javax.swing.JOptionPane;

import components.simplereader.SimpleReader;
import components.simplereader.SimpleReader1L;
import components.simplewriter.SimpleWriter;
import components.simplewriter.SimpleWriter1L;
import components.xmltree.XMLTree;
import components.xmltree.XMLTree1;

/**
 * A simple RSS reader that converts an XML RSS (version 2.0) feed from a
 * collection of popular news sites into the corresponding HTML output file onto
 * the users desktop.
 *
 *
 * @author William DeNiro
 *
 */
public final class RSSReader {

    /**
     * Private constructor so this utility class cannot be instantiated.
     */
    private RSSReader() {
    }

    /**
     * Outputs the "opening" tags in the generated HTML file. These are the
     * expected elements generated by this method:
     *
     * <html> <head> <title>the channel tag title as the page title</title>
     * </head> <body>
     * <h1>the page title inside a link to the <channel> link</h1>
     * <p>
     * the channel description
     * </p>
     * <table border="1">
     * <tr>
     * <th>Date</th>
     * <th>Source</th>
     * <th>News</th>
     * </tr>
     *
     * @param channel
     *            the channel element XMLTree
     * @param out
     *            the output stream
     * @updates out.content
     * @requires [the root of channel is a <channel> tag] and out.is_open
     * @ensures out.content = #out.content * [the HTML "opening" tags]
     */
    private static void outputHeader(XMLTree channel, SimpleWriter out) {
        assert channel != null : "Violation of: channel is not null";
        assert out != null : "Violation of: out is not null";
        assert channel.isTag() && channel.label() == ("channel") : ""
                + "Violation of: the label root of channel is a <channel> tag";
        assert out.isOpen() : "Violation of: out.is_open";

        String title = "";
        //Sets  title to the label of channel child label
        if (getChildElement(channel, "title") != -1) {
            title = channel.child(getChildElement(channel, "title")).child(0)
                    .label();
        } else if (getChildElement(channel, "description") > 0) {
            title = channel.child(getChildElement(channel, "description"))
                    .label();
        } else {
            // sets title to no information if empty
            title = "no information";
        }
        String description = "";

        if (getChildElement(channel, "description") == -1) {
            // sets description to "No description"due the description being -1
            // which means its out of range

            description = "No Description.";
        }

        if (channel.child(getChildElement(channel, "description"))
                .numberOfChildren() < 1) {
            description = "No Description.";
        } else {
            description = channel.child(getChildElement(channel, "description"))
                    .child(0).label();
        }

        String k = channel.child(getChildElement(channel, "link")).child(0)
                .label();
        //formats the html page
        out.println("<html>");
        out.println("<head>");
        out.println("<title>" + title + "</title>");
        out.println("</head>");
        out.println("<body>");
        out.println("<h1><a href=\"" + k + "\">" + title + "</a></h1>");
        out.println("<p>" + description + "</p>");
        out.println("<table border=\"1\">");
        out.println("<tr>");
        out.println("<th>Date</th>");
        out.println("<th>Source</th>");
        out.println("<th>News</th>");
        out.println("</tr>");
        int a = 0;

        while (channel.numberOfChildren() > a) {
            if (channel.child(a).label() == ("item")) {
                processItem(channel.child(a), out);
            }
            a++;
        }
    }

    /**
     * Outputs the "closing" tags in the generated HTML file. These are the
     * expected elements generated by this method:
     *
     *
     *
     * @param out
     *            the output stream
     * @updates out.contents
     * @requires out.is_open
     * @ensures out.content = #out.content * [the HTML "closing" tags]
     */
    private static void outputFooter(SimpleWriter out) {
        assert out != null : "Violation of: out is not null";
        assert out.isOpen() : "Violation of: out.is_open";
        //prints footer
        out.println("</table>");
        out.println("</body>");
        out.println("</html>");
    }

    /**
     * Finds the first occurrence of the given tag among the children of the
     * given {@code XMLTree} and return its index; returns -1 if not found.
     *
     * @param xml
     *            the {@code XMLTree} to search
     * @param tag
     *            the tag to look for
     * @return the index of the first child of type tag of the {@code XMLTree}
     *         or -1 if not found
     * @requires [the label of the root of xml is a tag]
     * @ensures <pre>
     * getChildElement =
     *  [the index of the first child of type tag of the {@code XMLTree} or
     *   -1 if not found]
     * </pre>
     */
    private static int getChildElement(XMLTree xml, String tag) {
        assert xml != null : "Violation of: xml is not null";
        assert tag != null : "Violation of: tag is not null";
        assert xml.isTag() : "Violation of: the label root of xml is a tag";

        int j = xml.numberOfChildren();
        int count = 0;
        int loc = -1;
        //if xml is a tag it enters the loop
        if (xml.isTag()) {
            while (count < j && loc < 0) {
                //if label at this index equals the string tag loc is set to the index
                if (xml.child(count).label().equals(tag)) {
                    loc = count;
                }
                count++;

            }
        }
        //returns the index of label
        return loc;
    }

    /**
     * Processes one news item and outputs one table row. The row contains three
     * elements: the publication date, the source, and the title (or
     * description) of the item.
     *
     * @param item
     *            the news item
     * @param out
     *            the output stream
     * @updates out.content
     * @requires [the label of the root of item is an <item> tag] and
     *           out.is_open
     * @ensures <pre>
     * out.content = #out.content *
     *   [an HTML table row with publication date, source, and title of news item]
     * </pre>
     */
    private static void processItem(XMLTree item, SimpleWriter out) {
        assert item != null : "Violation of: item is not null";
        assert out != null : "Violation of: out is not null";
        assert item.isTag() && item.label() == ("item") : ""
                + "Violation of: the label root of item is an <item> tag";
        assert out.isOpen() : "Violation of: out.is_open";

        out.println("<tr>");

        String pubDate = "";
        //finds the public data of the rss
        if (item.child(getChildElement(item, "pubDate"))
                .numberOfChildren() <= 1) {
            pubDate = "No Date Available";
        } else {
            pubDate = item.child(getChildElement(item, "pubDate")).child(0)
                    .label();
        }
        out.println("<th>" + pubDate + "</th>");

        String source = "No Source Available.";
        String sourceURL = "";
        int i = 0, e = -1;
        while (item.numberOfChildren() > i) {
            if (item.child(i).label() == ("source")) {
                e = 0;
            }
            i++;

        }
        if (e == 0) {
            source = item.child(getChildElement(item, "source")).child(0)
                    .label();
            sourceURL = item.child(getChildElement(item, "source"))
                    .attributeValue("url");
            out.println(
                    "<th><a href=\"" + sourceURL + "\">" + source + "</th>");
        } else {
            out.println("<th>" + source + "</th>");
        }

        String news = "No description";

        if (getChildElement(item, "description") > 0) {
            news = item.child(getChildElement(item, "description")).child(0)
                    .label();
        } else if (getChildElement(item, "title") > 0) {
            news = item.child(getChildElement(item, "title")).child(0).label();
        }
        //sets url to the element labeled link in the rss
        String url = "";
        if (getChildElement(item, "link") > 0) {
            url = item.child(getChildElement(item, "link")).child(0).label();
        }
        //prints url
        out.println("<th><a href=\"" + url + "\">" + news + "</th>");
        out.println("</tr>");
    }

    /**
     * Main method.
     *
     * @param args
     *            the command line arguments; unused here
     * @throws URISyntaxException
     * @throws IOException
     */
    public static void main(String[] args)
            throws IOException, URISyntaxException {
        SimpleReader in = new SimpleReader1L();
        SimpleWriter out = new SimpleWriter1L();
        boolean con = true;
        while (con) {
            //date base of popular news rss feeds
            String url = JOptionPane.showInputDialog(
                    "Enter a news site from a news source below \n - CNN"
                            + "\n -ABC" + "\n -CBS" + " \n -FOX " + "\n -ESPN"
                            + "\n -NYTimes" + "\n -Washington Post"
                            + "\n -Twitter");
            if (url.equals("cnn") || url.equals("CNN")) {
                url = "http://rss.cnn.com/rss/cnn_topstories.rss";
            }
            if (url.equals("abc") || url.equals("ABC")) {
                url = "http://feeds.abcnews.com/abcnews/topstories";
            }
            if (url.equals("cbs") || url.equals("CBS")) {
                url = "https://www.cbsnews.com/latest/rss/main";
            }
            if (url.equals("fox") || url.equals("FOX")) {
                url = "http://feeds.foxnews.com/foxnews/latest";
            }
            if (url.equals("espn") || url.equals("ESPN")) {
                url = "http://www.espn.com/espn/rss/news";
            }
            if (url.equals("nytimes") || url.equals("NYTimes")) {
                url = "http://rss.nytimes.com/services/xml/rss/nyt/HomePage.xml";
            }

            if (url.equals("washington post")
                    || url.equals("Washington Post")) {
                url = "http://feeds.washingtonpost.com/rss/rss_election-2012";
            }

            if (url.equals("twitter") || url.equals("Twitter")) {
                String name = JOptionPane.showInputDialog(
                        "Enter the username of the twitter feed you want");
                url = "https://twitrss.me/twitter_user_to_rss/?user=" + name;
            }

            XMLTree xml = new XMLTree1(url);

            // takes the url off the RSS feed
            String outfileName = JOptionPane
                    .showInputDialog("Enter an outputfile name ");
            //checks to see if the url entered is a valid RSS
            while (!xml.label().equals("rss") && !xml.hasAttribute("version")
                    && !xml.attributeValue("version").equals("2.0")) {
                out.print("\nEnter a valid URL of a RSS 2.0 feed: ");
                url = in.nextLine();
                if (url.equals("cnn")) {
                    url = "http://rss.cnn.com/rss/cnn_topstories.rss";
                }
                if (url.equals("abc")) {
                    url = "http://feeds.abcnews.com/abcnews/topstories";
                }
                if (url.equals("cbs")) {
                    url = "https://www.cbsnews.com/latest/rss/main";
                }
                if (url.equals("fox")) {
                    url = "http://feeds.foxnews.com/foxnews/latest";
                }

                if (url.equals("espn")) {
                    url = "http://www.espn.com/espn/rss/news";
                }

                if (url.equals("nytimes")) {
                    url = "https://archive.nytimes.com/www.nytimes.com/services/xml/rss/index.html?8dpc";
                }

                xml = new XMLTree1(url);
            }
            // sets a new file based on what the user types in
            String desktop = System.getProperty("user.home") + "/Desktop/";

            SimpleWriter fileOut = new SimpleWriter1L(
                    desktop + outfileName + ".html");

            // passes the xml child and the newly created file to the output header file
            outputHeader(xml.child(0), fileOut);
            outputFooter(fileOut);

            String more = JOptionPane.showInputDialog(
                    "Your RSS File is on your desktop! Would you like another website? If no type: no ");
            if (more.equals("no")) {
                con = false;
            }

            in.close();
            out.close();
        }
    }

}

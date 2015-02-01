package air.nikolaychernov.samis.ChernovPryb;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;

/**
 * Created by Nikolay on 01.02.2015.
 */
public class RoutesAndStopsXmlParser {
    // We don't use namespaces
    private static final String ns = null;

    public ArrayList<RouteStopBind> parse(String in) throws XmlPullParserException, IOException {
        return parse(new StringReader(in));
    }

    public ArrayList<RouteStopBind> parse(Reader in) throws XmlPullParserException, IOException {
        //Log.v("RoutesAndStopsXmlParser","Entered parse()");
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in);
            parser.nextTag();
            return readRoutes(parser);
        } finally {
            in.close();
        }
    }

    private ArrayList<RouteStopBind> readRoutes(XmlPullParser parser) throws XmlPullParserException, IOException {
        ArrayList<RouteStopBind> result = new ArrayList<RouteStopBind>();
        parser.require(XmlPullParser.START_TAG, ns, "routes");

        //Log.v("RoutesAndStopsXmlParser","Entered readRoutes()");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            ArrayList<RouteStopBind> tmp;
            // Starts by looking for the entry tag
            if (name.equals("route")) {
                tmp = readRoute(parser);
                result.addAll(tmp);
            } else {
                skip(parser);
            }
        }
        return result;
    }

    private ArrayList<RouteStopBind> readRoute(XmlPullParser parser) throws XmlPullParserException, IOException {
        ArrayList<RouteStopBind> result = new ArrayList<>();
        int KR_ID = 0;
        int KS_ID = 0;

        //Log.v("RoutesAndStopsXmlParser","Entered readRoute()");
        parser.require(XmlPullParser.START_TAG, ns, "route");

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();

            RouteStopBind tmp;

            if (name.equals("KR_ID")) {
                String data = readStringTag(parser, name);
                KR_ID = (int) Double.parseDouble(data);
            } else if (name.equals("stop")) {
                KS_ID = readStop(parser);
                tmp = new RouteStopBind(KR_ID, KS_ID);
                result.add(tmp);
                //Log.v("RoutesAndStopsXmlParser","Added: " + tmp.KR_ID + " + " + tmp.KS_ID);
            } else {
                skip(parser);
                //Log.v("RoutesAndStopsXmlParser","skipped");
            }
        }
            return result;

    }




    private int readStop(XmlPullParser parser) throws XmlPullParserException, IOException {
        int result = 0;

        //Log.v("RoutesAndStopsXmlParser","Entered readStop()");
        parser.require(XmlPullParser.START_TAG, ns, "stop");

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();

            if (name.equals("KS_ID")) {
                String data = readStringTag(parser, name);
                result = (int) Double.parseDouble(data);
            } else {
                skip(parser);
            }
        }

        return result;
    }

    private String readStringTag(XmlPullParser parser, String tagName) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, tagName);
        String result = readText(parser);
        //Log.v("RoutesAndStopsXmlParser","tagName = " + tagName);
        //Log.v("RoutesAndStopsXmlParser","result = " + result);
        parser.require(XmlPullParser.END_TAG, ns, tagName);
        return result;
    }

	/*
     * private Integer readIntTag(XmlPullParser parser, String tagName) throws
	 * IOException, XmlPullParserException {
	 * parser.require(XmlPullParser.START_TAG, ns, tagName); Integer result =
	 * Integer.parseInt(readText(parser)); parser.require(XmlPullParser.END_TAG,
	 * ns, tagName); return result; }
	 */

    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        /*if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }*/
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}

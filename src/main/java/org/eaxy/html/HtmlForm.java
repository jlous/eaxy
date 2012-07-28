package org.eaxy.html;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.eaxy.Element;

public class HtmlForm {

    private final Map<String, List<Element>> elementByNameIndex = new LinkedHashMap<String, List<Element>>();

    public HtmlForm(Element formElement) {
        for (Element element : formElement.find("...")) {
            if (element.name() == null) continue;
            if (!this.elementByNameIndex.containsKey(element.name())) {
                elementByNameIndex.put(element.name(), new ArrayList<Element>());
            }
            elementByNameIndex.get(element.name()).add(element);
        }
    }

    public List<String> getFieldNames() {
        ArrayList<String> names = new ArrayList<String>();
        for (String fieldName : elementByNameIndex.keySet()) {
            List<Element> elements = elementByNameIndex.get(fieldName);
            String tagName = elements.get(0).tagName().toLowerCase();
            if (tagName.equals("input") || tagName.equals("select") || tagName.equals("textarea")) {
                names.add(fieldName);
            }
        }
        return names;
    }

    public String get(String parameterName) {
        Element element = first(parameterName);
        if ("radio".equalsIgnoreCase(element.type())) {
            return selectedRadio(getElementByName(parameterName), element.name()).val();
        }
        return value(element, getElementByName(parameterName));
    }

    public HtmlForm set(String parameterName, String value) {
        for (Element element : getElementByName(parameterName)) {
            if (element.tagName().equalsIgnoreCase("textarea")) {
                element.text(value);
            } else if ("select".equalsIgnoreCase(element.tagName())) {
                for (Element option : element.find("...", "option")) {
                    option.selected(option.val().equalsIgnoreCase(value));
                }
            } else if ("checkbox".equalsIgnoreCase(element.type())) {
                element.checked(value != null);
            } else if ("radio".equalsIgnoreCase(element.type())) {
                for (Element radio : getElementByName(parameterName)) {
                    radio.checked(radio.val().equalsIgnoreCase(value));
                }
                return this;
            } else {
                element.attr("value", value);
            }
        }
        return this;
    }

    public void update(Map<String, String> values) {
        for (String parameterName : values.keySet()) {
            set(parameterName, values.get(parameterName));
        }
    }

    public Element first(String parameterName) {
        Iterator<Element> iterator = getElementByName(parameterName).iterator();
        if (!iterator.hasNext()) {
            throw new IllegalArgumentException(parameterName + " not found. Names: " + elementByNameIndex.keySet());
        }
        return iterator.next();
    }

    public List<String> getAll(String parameterName) {
        List<String> arrayList = new ArrayList<String>();
        for (Element element : getElementByName(parameterName)) {
            String value = value(element, getElementByName(parameterName));
            if (value != null) {
                arrayList.add(value);
            }
        }
        return arrayList;
    }

    private Iterable<Element> getElementByName(String parameterName) {
        return elementByNameIndex.get(parameterName);
    }

    private String value(Element element, Iterable<Element> iterable) {
        if ("radio".equalsIgnoreCase(element.type())) {
            if (element == selectedRadio(iterable, element.name())) {
                return element.val();
            } else {
                return null;
            }
        }
        if (element.tagName().equalsIgnoreCase("textarea")) {
            return element.text();
        }
        if (element.tagName().equalsIgnoreCase("select")) {
            for (Element option : element.find("...", "option")) {
                if (option.selected()) return option.val();
            }
            return null;
        }
        if ("checkbox".equalsIgnoreCase(element.type())) {
            return element.checked() ? "on" : null;
        }
        return element.val();
    }

    private Element selectedRadio(Iterable<Element> elements, String name) {
        Element result = null;
        for (Element element : elements) {
            if (element.name().equalsIgnoreCase(name) && element.checked()) {
                result = element;
            }
        }
        return result;
    }

    public String serialize() {
        // TODO: URL encode?
        StringBuilder result = new StringBuilder();
        for (String fieldName : getFieldNames()) {
            for (String value : getAll(fieldName)) {
                if (result.length() > 0) result.append("&");
                result.append(fieldName).append("=").append(value);
            }
        }
        return result.toString();
    }

    public Map<String,List<String>> toMap() {
        LinkedHashMap<String, List<String>> result = new LinkedHashMap<String, List<String>>();
        for (String fieldName : getFieldNames()) {
            result.put(fieldName, getAll(fieldName));
        }
        return result;
    }

}
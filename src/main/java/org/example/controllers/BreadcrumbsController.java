package org.example.controllers;

import java.util.Map;

public class BreadcrumbsController {

    private static final String ROOT = "/Gradebook";

    public static String getBreadcrumbs(Map<String, String> map) {
        StringBuilder sb = new StringBuilder();
        sb.append("<li><a href='").append(ROOT).append("'>").append("Меню</a></li>");
        int i = 0;
        for (Map.Entry<String, String> entry: map.entrySet()) {
            if (map.size() == ++i) {
                sb.append("<li>").append(entry.getKey()).append("</li>");
            } else {
                sb.append("<li><a href='").append(ROOT).append(entry.getValue()).append("'>")
                        .append(entry.getKey()).append("</a></li>");
            }
        }
        return sb.toString();
    }
}

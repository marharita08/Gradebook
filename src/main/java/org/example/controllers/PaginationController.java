package org.example.controllers;

public class PaginationController {
    private int totalItems;
    private int itemsPerPage;
    private int currentPageNumber;

    public PaginationController(int totalItems, int itemsPerPage, int currentPageNumber) {
        this.totalItems = totalItems;
        this.itemsPerPage = itemsPerPage;
        this.currentPageNumber = currentPageNumber;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    public int getItemsPerPage() {
        return itemsPerPage;
    }

    public void setItemsPerPage(int itemsPerPage) {
        this.itemsPerPage = itemsPerPage;
    }

    public int getCurrentPageNumber() {
        return currentPageNumber;
    }

    public void setCurrentPageNumber(int currentPageNumber) {
        this.currentPageNumber = currentPageNumber;
    }

    public String makePagingLinks(String pageLocation) {
        StringBuilder sb = new StringBuilder();
        if(totalItems <= itemsPerPage)
            return sb.toString();
        int totalPages = (totalItems/itemsPerPage);
        if(totalItems % itemsPerPage != 0)
            totalPages++;
        if (currentPageNumber > totalPages)
            currentPageNumber = 1;
        if(totalItems <= currentPageNumber * itemsPerPage)
            currentPageNumber = totalPages;
        int start = currentPageNumber - 3;
        if(start <= 0)
            start = 1;
        int end = currentPageNumber + 3;
        if(end >= totalPages)
            end = totalPages;
        if(start > 1) {
            sb.append("<li><a href='").append(pageLocation);
            sb.append("?page=1'>1</a></li>");
        }
        if (start > 2)
            sb.append("<li><a disabled>...</a></li>");
        for (int i = start; i <= end; i++) {
            if(i == currentPageNumber) {
                sb.append("<li><a disabled style=\"font-weight: bold;\">");
                sb.append(i);
                sb.append("</a></li>");
            } else {
                sb.append("<li><a href='").append(pageLocation);
                sb.append("?page=").append(i).append("'>");
                sb.append(i).append("</a></li>");
            }
            if(i < totalPages)
                sb.append(" ");
        }
        if(end + 1 < totalPages)
            sb.append("<li><a disabled>...</a></li>");
        if (end < totalPages) {
            if (totalPages == currentPageNumber) {
                sb.append("<li><strong>");
                sb.append(totalPages);
                sb.append("</strong></li>");
            } else {
                sb.append("<li><a href='").append(pageLocation);
                sb.append("?page=").append(totalPages).append("'>");
                sb.append(totalPages).append("</a></li>");
            }
        }
        return sb.toString();
    }
}

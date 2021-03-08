package mx.com.nmp.eventos.model.page;

import org.springframework.beans.support.PagedListHolder;

public class Page {

    public static PagedListHolder pagination;

    public void setPagination(PagedListHolder pagination) {
        Page.pagination = pagination;
    }

}

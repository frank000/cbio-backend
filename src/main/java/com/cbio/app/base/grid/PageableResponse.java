package com.cbio.app.base.grid;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class PageableResponse<T extends Serializable> implements Serializable {

    private static final long serialVersionUID = 7021647731034913278L;
    private Long total;
    private List<T> items;

    public Long getTotal() {
        return this.total;
    }

    public List<T> getItems() {
        return this.items;
    }

    public void setTotal(final Long total) {
        this.total = total;
    }

    public void setItems(final List<T> items) {
        this.items = items;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof PageableResponse)) {
            return false;
        } else {
            PageableResponse<?> other = (PageableResponse)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                Object this$total = this.getTotal();
                Object other$total = other.getTotal();
                if (this$total == null) {
                    if (other$total != null) {
                        return false;
                    }
                } else if (!this$total.equals(other$total)) {
                    return false;
                }

                Object this$items = this.getItems();
                Object other$items = other.getItems();
                if (this$items == null) {
                    if (other$items != null) {
                        return false;
                    }
                } else if (!this$items.equals(other$items)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(final Object other) {
        return other instanceof PageableResponse;
    }

    @Override
    public int hashCode() {
        return Objects.hash(total, items);
    }

    public String toString() {
        Long var10000 = this.getTotal();
        return "PageableResponseModel(total=" + var10000 + ", items=" + this.getItems() + ")";
    }

    private PageableResponse(final Long total, final List<T> items) {
        this.total = total;
        this.items = items;
    }

    public static <T extends Serializable> PageableResponse<T> of(final Long total, final List<T> items) {
        return new PageableResponse(total, items);
    }
}

package sebastians.sportan.tasks;

import java.util.List;

public interface GetListInterface<T> {
    List<T> getList(int limit);
}

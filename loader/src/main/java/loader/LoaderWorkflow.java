package loader;

import java.util.concurrent.Callable;

public interface LoaderWorkflow extends Callable<LoaderWorkflow> {
}

package export.source;


import com.google.common.base.Supplier;
import export.model.ExportSpecification;
import export.model.ExpressionContext;

/**
 * Created by shengli on 12/28/15.
 */
public interface ContextSource extends Supplier<Iterable<ExpressionContext>> {

    /**
     * Appears here for clarity only.
     * Implementers provide expression contexts via an iterable here.
     *
     * @return An iterable over expression contexts
     */
    @Override
    Iterable<ExpressionContext> get();

    /**
     * Provide the specification to generate the contexts.
     *
     * @param exportSpecification the given
     */
    void setExportSpecification(ExportSpecification exportSpecification);
}

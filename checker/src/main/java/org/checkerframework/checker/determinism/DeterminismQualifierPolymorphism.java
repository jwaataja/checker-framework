package org.checkerframework.checker.determinism;

import java.util.Map;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.type.TypeMirror;
import org.checkerframework.framework.type.AnnotatedTypeMirror;
import org.checkerframework.framework.type.poly.DefaultQualifierPolymorphism;
import org.checkerframework.framework.util.AnnotationMirrorMap;
import org.checkerframework.framework.util.AnnotationMirrorSet;
import org.checkerframework.javacutil.TypesUtils;

// TODO: Why are these specific, such as "the return type"?  I would expect all @PolyDet("up") to be
// resolved, not just those on return types, and similarly for @PolyDet("down") and @PolyDet("use").
/**
 * Resolves polymorphic annotations at method invocations as follows:
 *
 * <ol>
 *   <li>Resolves the return type annotated as {@code @PolyDet("up")} to {@code @NonDet} if the
 *       least upper bound of argument types resolves to {@code OrderNonDet}.
 *   <li>Resolves the return type annotated as {@code @PolyDet("down")} to {@code @Det} if the least
 *       upper bound of argument types resolves to {@code OrderNonDet}.
 *   <li>Resolves an argument type annotated as {@code @PolyDet("use")} to the same annotation that
 *       {@code @PolyDet} resolves to for the other arguments.
 * </ol>
 */
public class DeterminismQualifierPolymorphism extends DefaultQualifierPolymorphism {

    /** Determinism Checker type factory. */
    DeterminismAnnotatedTypeFactory factory;

    /**
     * Creates a {@link DefaultQualifierPolymorphism} instance that uses the Determinism Checker for
     * querying type qualifiers and the {@link DeterminismAnnotatedTypeFactory} for getting
     * annotated types.
     *
     * @param env the processing environment
     * @param factory the type factory for the Determinism Checker
     */
    public DeterminismQualifierPolymorphism(
            ProcessingEnvironment env, DeterminismAnnotatedTypeFactory factory) {
        super(env, factory);
        this.factory = factory;
    }

    /**
     * Replaces {@code @PolyDet} in {@code type} with the instantiations in {@code replacements}.
     * Replaces {@code @PolyDet("up")} with {@code @NonDet} if it resolves to {@code OrderNonDet}.
     * Replaces {@code @PolyDet("down")} with {@code @Det} if it resolves to {@code OrderNonDet}.
     * Replaces {@code @PolyDet("use")} with the same annotation that {@code @PolyDet} resolves to.
     *
     * @param type annotated type whose poly annotations are replaced
     * @param replacements mapping from polymorphic annotation to instantiation
     */
    @Override
    protected void replace(
            AnnotatedTypeMirror type, AnnotationMirrorMap<AnnotationMirrorSet> replacements) {
        boolean isPolyUp = false;
        boolean isPolyDown = false;
        if (type.hasAnnotation(factory.POLYDET_UP)) {
            isPolyUp = true;
            type.replaceAnnotation(factory.POLYDET);
        } else if (type.hasAnnotation(factory.POLYDET_DOWN)) {
            isPolyDown = true;
            type.replaceAnnotation(factory.POLYDET);
        }

        if (type.hasAnnotation(factory.POLYDET) || type.hasAnnotation(factory.POLYDET_USE)) {
            Map.Entry<AnnotationMirror, AnnotationMirrorSet> pqentry =
                    replacements.entrySet().iterator().next();
            AnnotationMirrorSet quals = pqentry.getValue();
            type.replaceAnnotations(quals);
        }

        if (type.hasAnnotation(factory.ORDERNONDET)) {
            if (isPolyUp) {
                replaceOrderNonDet(type, factory.NONDET);
            }
            if (isPolyDown) {
                replaceOrderNonDet(type, factory.DET);
            }
        }
    }

    // TODO: Is there a precondition that `type` must have an @OrderNonDet annotation?  If so, state
    // it.
    /**
     * Replaces the @OrderNonDet annotation of {@code type} with {@code replaceType}.
     *
     * @param type the polymorphic type to be replaced
     * @param replaceType the type to be replaced with
     */
    private void replaceOrderNonDet(AnnotatedTypeMirror type, AnnotationMirror replaceType) {
        type.replaceAnnotation(replaceType);

        // This check succeeds for @OrderNonDet Set<T> (Generic types)
        if (TypesUtils.getTypeElement(type.getUnderlyingType()) == null) {
            return;
        }

        // TODO-rashmi: Handle Maps
        recursiveReplaceAnnotation(type, replaceType);
    }

    // TODO: The documentation states that this unconditionally replaces annoattions, but the code
    // seems to check whether there is already an @OrderNonDet annotation present.  Either the code
    // or the documentation is wrong.  Please make sure that the specification fully describes the
    // behavior.  Otherwise, it's misleading, and is very confusing for readers.
    /**
     * Iterates over all the nested Collection/Iterator type arguments of {@code type} and replaces
     * their top-level annotations with {@code replaceType}. Example: If this method is called with
     * {@code type} as {@code @OrderNonDet Set<@OrderNonDet Set<@Det Integer>>} and {@code
     * replaceType} as {@code @NonDet}, the result will be {@code @NonDet Set<@NonDet Set<@Det
     * Integer>>}.
     */
    void recursiveReplaceAnnotation(AnnotatedTypeMirror type, AnnotationMirror replaceType) {
        TypeMirror underlyingTypeOfReceiver =
                TypesUtils.getTypeElement(type.getUnderlyingType()).asType();
        if (!(factory.isCollection(underlyingTypeOfReceiver)
                || factory.isIterator(underlyingTypeOfReceiver))) {
            return;
        }

        AnnotatedTypeMirror.AnnotatedDeclaredType declaredTypeOuter =
                (AnnotatedTypeMirror.AnnotatedDeclaredType) type;
        AnnotatedTypeMirror argType = declaredTypeOuter.getTypeArguments().get(0);
        if (argType.hasAnnotation(factory.ORDERNONDET)) {
            argType.replaceAnnotation(replaceType);
        }
        recursiveReplaceAnnotation(argType, replaceType);
    }
}

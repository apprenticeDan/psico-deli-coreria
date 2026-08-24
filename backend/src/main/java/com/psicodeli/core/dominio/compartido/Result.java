package com.psicodeli.core.dominio.compartido;

import java.util.Optional;
import java.util.function.Function;

public sealed interface Result<T, E> permits Result.Ok, Result.Error {

    static <T, E> Result<T, E> ok(T value) {
        return new Ok<>(value);
    }

    static <T, E> Result<T, E> error(E error) {
        return new Error<>(error);
    }

    boolean isOk();
    boolean isError();

    <U> Result<U, E> map(Function<? super T, ? extends U> mapper);
    <U> Result<U, E> flatMap(Function<? super T, ? extends Result<U, E>> mapper);

    T getValue();
    E getError();

    record Ok<T, E>(T value) implements Result<T, E> {
        @Override
        public boolean isOk() { return true; }
        @Override
        public boolean isError() { return false; }
        @Override
        public <U> Result<U, E> map(Function<? super T, ? extends U> mapper) {
            return new Ok<>(mapper.apply(value));
        }
        @Override
        public <U> Result<U, E> flatMap(Function<? super T, ? extends Result<U, E>> mapper) {
            return mapper.apply(value);
        }
        @Override
        public T getValue() { return value; }
        @Override
        public E getError() { throw new UnsupportedOperationException("No error in Ok result"); }
    }

    record Error<T, E>(E error) implements Result<T, E> {
        @Override
        public boolean isOk() { return false; }
        @Override
        public boolean isError() { return true; }
        @Override
        public <U> Result<U, E> map(Function<? super T, ? extends U> mapper) {
            return new Error<>(error);
        }
        @Override
        public <U> Result<U, E> flatMap(Function<? super T, ? extends Result<U, E>> mapper) {
            return new Error<>(error);
        }
        @Override
        public T getValue() { throw new UnsupportedOperationException("No value in Error result"); }
        @Override
        public E getError() { return error; }
    }
}

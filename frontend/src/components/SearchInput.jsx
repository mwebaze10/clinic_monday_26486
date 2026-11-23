import React from "react";

export default function SearchInput({
  value,
  onChange,
  inputRef,
  placeholder = "Search",
  onClear,
}) {
  return (
    <label className="search-box">
      <span className="search-icon" aria-hidden="true" />
      <span className="visually-hidden">{placeholder}</span>
      <input
        ref={inputRef}
        type="search"
        placeholder={placeholder}
        autoComplete="off"
        value={value}
        onChange={(event) => onChange(event.target.value)}
      />
      {value && onClear ? (
        <button className="search-clear" type="button" onClick={onClear} aria-label="Clear search">
          &times;
        </button>
      ) : (
        <kbd aria-hidden="true">/</kbd>
      )}
    </label>
  );
}

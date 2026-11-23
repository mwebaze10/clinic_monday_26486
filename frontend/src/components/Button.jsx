import React from "react";

export default function Button({
  children,
  variant = "secondary",
  className = "",
  type = "button",
  ...props
}) {
  return (
    <button
      className={`button button-${variant}${className ? ` ${className}` : ""}`}
      type={type}
      {...props}
    >
      {children}
    </button>
  );
}

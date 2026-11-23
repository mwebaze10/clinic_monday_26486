import React, { useCallback, useEffect, useMemo, useRef, useState } from "react";

import Button from "./components/Button.jsx";
import ConfirmationModal from "./components/ConfirmationModal.jsx";
import SearchInput from "./components/SearchInput.jsx";

const apiBaseUrl = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";
const officesEndpoint = `${apiBaseUrl}/api/offices`;

function getErrorMessage(error) {
  if (error instanceof TypeError) {
    return `Cannot connect to ${officesEndpoint}. Start the Spring Boot API and PostgreSQL, then refresh.`;
  }
  return error.message;
}

export default function App() {
  const [offices, setOffices] = useState([]);
  const [search, setSearch] = useState("");
  const [loading, setLoading] = useState(true);
  const [deletingId, setDeletingId] = useState(null);
  const [officeToDelete, setOfficeToDelete] = useState(null);
  const [message, setMessage] = useState("");
  const [hasError, setHasError] = useState(false);
  const searchInput = useRef(null);

  const filteredOffices = useMemo(() => {
    const query = search.trim().toLocaleLowerCase();
    return offices.filter((office) => (
      (office.name ?? "").toLocaleLowerCase().includes(query)
      || String(office.officeNumber ?? "").toLocaleLowerCase().includes(query)
    ));
  }, [offices, search]);

  const loadOffices = useCallback(async (signal) => {
    setLoading(true);
    setMessage("");
    setHasError(false);

    try {
      const response = await fetch(officesEndpoint, {
        headers: { Accept: "application/json" },
        signal,
      });
      if (!response.ok) {
        throw new Error(`Request failed (${response.status} ${response.statusText})`);
      }

      const result = await response.json();
      if (!Array.isArray(result)) {
        throw new Error("The offices endpoint returned an unexpected response.");
      }

      setOffices(result);
    } catch (error) {
      if (error.name === "AbortError") {
        return;
      }
      setOffices([]);
      setHasError(true);
      setMessage(`Unable to load offices: ${getErrorMessage(error)}`);
    } finally {
      if (!signal?.aborted) {
        setLoading(false);
      }
    }
  }, []);

  useEffect(() => {
    const controller = new AbortController();
    loadOffices(controller.signal);
    return () => controller.abort();
  }, [loadOffices]);

  useEffect(() => {
    function focusSearch(event) {
      if (event.key === "/" && document.activeElement !== searchInput.current) {
        event.preventDefault();
        searchInput.current?.focus();
      }
    }

    document.addEventListener("keydown", focusSearch);
    return () => document.removeEventListener("keydown", focusSearch);
  }, []);

  async function deleteOffice() {
    if (!officeToDelete) {
      return;
    }

    const office = officeToDelete;
    const officeLabel = office.name || `office ${office.officeNumber ?? office.id}`;
    setDeletingId(office.id);
    setMessage("");
    setHasError(false);
    try {
      const response = await fetch(`${officesEndpoint}/${encodeURIComponent(office.id)}`, {
        method: "DELETE",
      });
      if (!response.ok) {
        const responseBody = (await response.text()).trim();
        let details = responseBody;
        if (responseBody) {
          try {
            const errorBody = JSON.parse(responseBody);
            details = errorBody.message || errorBody.detail || responseBody;
          } catch {
            details = responseBody;
          }
        }
        throw new Error(details || `Request failed (${response.status} ${response.statusText})`);
      }

      setOffices((currentOffices) => currentOffices.filter((item) => item.id !== office.id));
      setMessage(`${officeLabel} was deleted.`);
    } catch (error) {
      setHasError(true);
      setMessage(`Unable to delete ${officeLabel}: ${getErrorMessage(error)}`);
    } finally {
      setDeletingId(null);
      setOfficeToDelete(null);
    }
  }

  const showEmptyState = !loading && !hasError && filteredOffices.length === 0;
  const noOffices = offices.length === 0;

  return (
    <main className="page-shell">
      <header className="topbar">
        <a className="brand" href="#" aria-label="Kigali Clinic home">
          <span className="brand-mark" aria-hidden="true">K</span>
          <span>Kigali Clinic</span>
        </a>
        <span className="topbar-label">Administration</span>
      </header>

      <section className="page-content" aria-labelledby="page-title">
        <div className="breadcrumbs">Clinic management <span>/</span> Offices</div>

        <div className="page-heading">
          <div>
            <p className="eyebrow">Facilities</p>
            <h1 id="page-title">Office directory</h1>
            <p className="subtitle">Browse and manage the clinic&apos;s available offices.</p>
          </div>
          <div className="count-card" aria-live="polite">
            <span className="count-label">Offices shown</span>
            <strong>{loading || hasError ? "—" : filteredOffices.length}</strong>
          </div>
        </div>

        <section className="directory-card" aria-label="Office directory">
          <div className="toolbar">
            <SearchInput
              inputRef={searchInput}
              value={search}
              onChange={setSearch}
              onClear={() => setSearch("")}
              placeholder="Search by office name or number"
            />
            <Button onClick={() => loadOffices()} disabled={loading}>
              <span aria-hidden="true">↻</span> Refresh
            </Button>
          </div>

          <p className={`page-message${hasError ? " error" : ""}`} role="status" aria-live="polite">
            {loading ? "Loading offices…" : message}
          </p>

          <div className="table-wrap">
            <table>
              <thead>
                <tr>
                  <th scope="col">Office number</th>
                  <th scope="col">Office name</th>
                  <th scope="col" className="actions-heading">Action</th>
                </tr>
              </thead>
              <tbody>
                {filteredOffices.map((office) => (
                  <tr key={office.id}>
                    <td>
                      <span className="office-number">
                        {office.officeNumber == null ? "—" : office.officeNumber}
                      </span>
                    </td>
                    <td className="office-name">{office.name || "Unnamed office"}</td>
                    <td className="actions-cell">
                      <div className="action-buttons">
                        <Button
                          variant="update"
                          disabled
                          title="Office updates are not implemented yet"
                        >
                          Update
                        </Button>
                        <Button
                          variant="delete"
                          disabled={deletingId === office.id}
                          onClick={() => setOfficeToDelete(office)}
                        >
                          Delete
                        </Button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          {showEmptyState && (
            <div className="empty-state">
              <span className="empty-icon" aria-hidden="true">⌕</span>
              <strong>{noOffices ? "No offices available" : "No offices found"}</strong>
              <span>
                {noOffices
                  ? "Offices will appear here once they have been added to the clinic."
                  : "Try another name or office number."}
              </span>
              {search && <Button onClick={() => setSearch("")}>Clear search</Button>}
            </div>
          )}
        </section>

        <footer className="page-footer">
          <span><span className="status-dot" aria-hidden="true" /> Clinic API</span>
          <span>Office directory</span>
        </footer>
      </section>

      <ConfirmationModal
        isOpen={Boolean(officeToDelete)}
        title="Delete office?"
        message={`This will permanently delete ${officeToDelete?.name || "this office"}. This action cannot be undone.`}
        confirmLabel="Delete office"
        isBusy={deletingId !== null}
        onConfirm={deleteOffice}
        onCancel={() => setOfficeToDelete(null)}
      />
    </main>
  );
}

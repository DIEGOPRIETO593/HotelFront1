/**
 * Custom JS for Hotel Management System
 */

"use strict";

document.addEventListener("DOMContentLoaded", function() {
    // Implement global real-time search bar for all data tables in cards
    const tableContainers = document.querySelectorAll(".card .table-responsive");
    
    tableContainers.forEach(function(container) {
        const table = container.querySelector("table");
        if (!table) return;
        
        const tbody = table.querySelector("tbody");
        if (!tbody) return;
        
        // Count initial rows (excluding any empty state rows if any)
        const allRows = Array.from(tbody.querySelectorAll("tr"));
        if (allRows.length === 0) return;
        
        // Create search bar container
        const searchHeader = document.createElement("div");
        searchHeader.className = "p-3 bg-white border-bottom d-flex align-items-center justify-content-between flex-wrap gap-3";
        searchHeader.style.borderTopLeftRadius = "1rem";
        searchHeader.style.borderTopRightRadius = "1rem";
        
        searchHeader.innerHTML = `
            <div class="input-group shadow-sm" style="max-width: 400px; min-width: 280px; box-shadow: 0 2px 6px 0 rgba(114, 124, 245, 0.05); border-radius: 0.5rem; overflow: hidden; border: 1px solid #e4e6fc;">
                <div class="input-group-prepend" style="display: flex;">
                    <span class="input-group-text bg-light border-0 text-primary px-3" style="background-color: #fdfdff !important; font-size: 1rem; display: flex; align-items: center;"><i class="fas fa-search"></i></span>
                </div>
                <input type="text" class="form-control bg-light border-0 py-2 table-search-input" placeholder="Buscar en la tabla (ej. nombre, ID, estado)..." style="background-color: #fdfdff !important; font-size: 0.9rem; box-shadow: none; height: 42px;">
                <div class="input-group-append" style="display: none;">
                    <button class="btn btn-light text-secondary border-0 px-3 table-search-clear" type="button" title="Limpiar búsqueda" style="background-color: #fdfdff !important; height: 42px; display: flex; align-items: center;"><i class="fas fa-times"></i></button>
                </div>
            </div>
            <div class="d-flex align-items-center">
                <span class="badge bg-light text-secondary border px-3 py-2 fw-medium table-search-counter" style="font-size: 0.85rem; border-color: #e4e6fc !important; background-color: #fdfdff !important;">
                    <i class="fas fa-list-ul me-1" style="margin-right: 6px;"></i> <span class="counter-text">${allRows.length} registros en total</span>
                </span>
            </div>
        `;
        
        // Insert before table-responsive inside card-body
        container.parentNode.insertBefore(searchHeader, container);
        
        const searchInput = searchHeader.querySelector(".table-search-input");
        const clearBtnContainer = searchHeader.querySelector(".input-group-append");
        const clearBtn = searchHeader.querySelector(".table-search-clear");
        const counterSpan = searchHeader.querySelector(".counter-text");
        const counterIcon = searchHeader.querySelector(".table-search-counter i");
        
        // Create no results row
        const colCount = table.querySelectorAll("thead th").length || 10;
        const noResultsRow = document.createElement("tr");
        noResultsRow.className = "no-results-row";
        noResultsRow.style.display = "none";
        noResultsRow.innerHTML = `<td colspan="${colCount}" class="text-center py-5 text-muted"><i class="fas fa-search mb-2 d-block" style="font-size: 2rem; opacity: 0.4;"></i>No se encontraron resultados coincidentes para tu búsqueda</td>`;
        tbody.appendChild(noResultsRow);
        
        function filterTable() {
            const query = searchInput.value.toLowerCase().trim();
            let visibleCount = 0;
            
            if (query.length > 0) {
                clearBtnContainer.style.display = "flex";
            } else {
                clearBtnContainer.style.display = "none";
            }
            
            allRows.forEach(function(row) {
                if (row.classList.contains("no-results-row")) return;
                
                const text = row.textContent.toLowerCase();
                if (text.includes(query)) {
                    row.style.display = "";
                    visibleCount++;
                } else {
                    row.style.display = "none";
                }
            });
            
            if (visibleCount === 0 && query.length > 0) {
                noResultsRow.style.display = "";
                counterIcon.className = "fas fa-exclamation-circle text-warning me-1";
                counterIcon.style.marginRight = "6px";
                counterSpan.textContent = `0 resultados de ${allRows.length}`;
            } else {
                noResultsRow.style.display = "none";
                if (query.length > 0) {
                    counterIcon.className = "fas fa-filter text-primary me-1";
                    counterIcon.style.marginRight = "6px";
                    counterSpan.textContent = `Mostrando ${visibleCount} de ${allRows.length} registros`;
                } else {
                    counterIcon.className = "fas fa-list-ul me-1";
                    counterIcon.style.marginRight = "6px";
                    counterSpan.textContent = `${allRows.length} registros en total`;
                }
            }
        }
        
        searchInput.addEventListener("input", filterTable);
        clearBtn.addEventListener("click", function() {
            searchInput.value = "";
            filterTable();
            searchInput.focus();
        });
    });
});

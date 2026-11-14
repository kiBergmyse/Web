class Graph {
    constructor(canvasId) {
        this.canvas = document.getElementById(canvasId);
        this.ctx = this.canvas.getContext("2d");
        this.width = this.canvas.width;
        this.height = this.canvas.height;

        this.draw();
    }

    draw() {
        const ctx = this.ctx;
        const x = this.width;
        const y = this.height;

        // Фон
        ctx.beginPath();
        ctx.lineWidth = 1;
        ctx.fillStyle = "white";
        ctx.strokeStyle = "black";
        ctx.moveTo(0, 0);
        ctx.lineTo(0, y);
        ctx.lineTo(x, y);
        ctx.lineTo(x, 0);
        ctx.closePath();
        ctx.fill();

        ctx.lineWidth = 2;

        // Области
        ctx.fillStyle = "RoyalBlue";
	
        ctx.fillRect(x / 3.3333, y / 3.3333, x / 5, y / 5);

        ctx.beginPath();

        ctx.moveTo(x / 2, y / 2);
        ctx.lineTo(x / 2 + y / 5, y / 2);
        ctx.lineTo(x / 2, y / 2 + y / 5);
        ctx.moveTo(x / 2, y / 2);

        ctx.arc(x / 2, y / 2,  x / 5, Math.PI / 2, Math.PI );
        ctx.fill();


        // Ось
        ctx.beginPath();
        ctx.strokeStyle = "black";
        ctx.moveTo(x / 2, 0);
        ctx.lineTo(x / 2, y);
        ctx.moveTo(0, y / 2);
        ctx.lineTo(x, y / 2);

        ctx.stroke();


        // Отметки на осях
        ctx.beginPath();
        ctx.fillStyle = "black";
        ctx.font = "20px Times New Roman";
        //y
        for (let i = -2; i <= 2; i++) {
            const px = x / 2 + i * x / 5;

            ctx.moveTo(px, y / 2 - 5);
            ctx.lineTo(px, y / 2 + 5);

            if (i === -2) {
                ctx.fillText("-R", px - 3, y / 2 + 25);
            } else if (i === -1) {
                ctx.fillText("-R/2", px - 3, y / 2 + 25);
            } else if (i === 2) {
                ctx.fillText("R", px - 3, y / 2 + 25);
            } else if (i === 1) {
                ctx.fillText("R/2", px - 3, y / 2 + 25);
            }
        }
        //x
        for (let i = -2; i <= 2; i++) {
            const py = y / 2 + i * y / 5;

            ctx.moveTo(x / 2 - 5, py);
            ctx.lineTo(x / 2 + 5, py);

            if (i === -2) {
                ctx.fillText("R", x / 2 + 10, py - 3);
            } else if (i === -1) {
                ctx.fillText("R/2", x / 2 + 10, py - 3);
            } else if (i === 2) {
                ctx.fillText("-R", x / 2 + 10, py - 3);
            } else if (i === 1) {
                ctx.fillText("-R/2", x / 2 + 10, py - 3);
            }
        }
        ctx.stroke();
    }
}

class Checker {
    static validate(xValue, yText, rValue) {
        if (!xValue) {
            return "Выберите X";}
        if (!yText) {
            return "Введите Y";}

        const y = Number(yText.replace(",", "."));

        if (!Number.isFinite(y) || y <= -5 || y >= 5) {
            return "Введите корректный Y";}
        return null;
    }
}

class ResultStorage {
    constructor() {
        this.key = "results";
    }

    load() {
        return JSON.parse(localStorage.getItem(this.key)) || [];
    }

    save(result) {
        const results = this.load();
        results.push(result);
        localStorage.setItem(this.key, JSON.stringify(results));
    }

    getPage(options) {
        const pageSizeValue = Number(options.pageSize);
        const pageSize = Number.isInteger(pageSizeValue) && pageSizeValue >= 0 ? pageSizeValue : 10;
        const results = this.load();

        const filtered = results.filter(result => {
            if (options.hitFilter === "hit" && !result.hit) {
                return false;}

            if (options.hitFilter === "miss" && result.hit) {
                return false;}

            if (
                options.rFilter !== "all" &&
                Number(result.r) !== Number(options.rFilter)
            ) {
                return false;}
            return true;
        });

        filtered.sort((a, b) => {
            if (options.sortOrder === "0") {
                return a.time - b.time;}
            return b.time - a.time;
        });

        const total = filtered.length;
        const totalPages = Math.max(1, Math.ceil(total / pageSize));

        const requestedPage = Number.parseInt(options.page, 10);
        const page = Math.min(Math.max(Number.isInteger(requestedPage) ? requestedPage : 1, 1), totalPages);

        const start = (page - 1) * pageSize;
        const items = filtered.slice(start, start + pageSize);

        return {items: items, total: total, totalPages: totalPages, page: page};
    }
}


class ResultTable {
    constructor(tableId) {
        this.table = document.getElementById(tableId);
    }

    addRow(result) {
        const row = this.table.insertRow(-1);

        row.insertCell(0).textContent = result.x;
        row.insertCell(1).textContent = result.y;
        row.insertCell(2).textContent = result.r;

        const hitCell = row.insertCell(3);

        if (result.hit) {
            hitCell.textContent = "да";
            hitCell.style.backgroundColor = "SeaGreen";
        } else {
            hitCell.textContent = "нет";
            hitCell.style.backgroundColor = "FireBrick";
        }

        row.insertCell(4).textContent =
            new Date(result.time).toLocaleString("ru-RU");

        row.insertCell(5).textContent =
            result.execNanos != null ? (result.execNanos / 1000).toFixed(0) + " мкс" : "—";
    }

    render(results) {
        while (this.table.rows.length > 2) {
            this.table.deleteRow(2);
        }

        for (const result of results) {
            this.addRow(result);
        }
    }
}

class App {
    constructor() {
        this.form = document.getElementById("pointForm");
        this.errorMessage = document.getElementById("errorMessage");
        this.checkButton = document.getElementById("checkButton");

        this.pageSizeInput = document.getElementById("size");
        this.pageNumberInput = document.getElementById("pageNumber");
        this.timeSort = document.getElementById("timeSort");
        this.hitFilter = document.getElementById("hitFilter");
        this.rFilter = document.getElementById("rFilter");
        this.pageInfo = document.getElementById("pageInfo");

        this.graph = new Graph("maingraph");
        this.resultTable = new ResultTable("mainTable");
        this.storage = new ResultStorage();

        this.Filter();

        document.querySelectorAll(".filter_buttons button").forEach(button => {
            button.addEventListener("click", () => {
                const select = document.getElementById(button.dataset.target);
                select.value = button.dataset.value;

                document.querySelectorAll(`.filter_buttons button[data-target="${button.dataset.target}"]`
                ).forEach(b => b.classList.remove("active"));

                button.classList.add("active");
                select.dispatchEvent(new Event("change"));
            });
        });

        this.checkButton.addEventListener("click", () => this.check());
        const controls = [
            this.pageSizeInput,
            this.timeSort,
            this.hitFilter,
            this.rFilter
        ];

        for (const control of controls) {
            control.addEventListener("change", () => {
                this.pageNumberInput.value = "1";
                this.updateResults();
            });
        }

        this.pageNumberInput.addEventListener("change", () => {
            this.updateResults();
        });

        this.updateResults();
    }

    Filter() {
        const source = this.form.querySelector('select[name="r"]');
        const values = new Set();

        for (const sourceOption of source.options) {
            const value = sourceOption.value.trim();

            if (value === "" || values.has(value)) {
                continue;
            }

            values.add(value);

            const option = document.createElement("option");
            option.value = value;
            option.textContent = sourceOption.textContent.trim() || value;

            this.rFilter.appendChild(option);
        }
    }

    updateResults() {
        const pageData = this.storage.getPage({
            page: this.pageNumberInput.value,
            pageSize: this.pageSizeInput.value,
            sortOrder: this.timeSort.value,
            hitFilter: this.hitFilter.value,
            rFilter: this.rFilter.value
        });
        this.pageNumberInput.value = pageData.page;
        this.pageNumberInput.max = pageData.totalPages;

        this.pageInfo.textContent =
            `Страница №${pageData.page} из ${pageData.totalPages}\n` +
            `Результатов: ${pageData.total}`;

        this.resultTable.render(pageData.items);
    }

    async check() {
        const xValue = this.form.querySelector('input[name="x"]:checked');
        const yValue = this.form.querySelector('input[name="y"]');
        const rValue = this.form.querySelector('select[name="r"]');

        const yText = yValue.value.trim();
        const error = Checker.validate(xValue, yText, rValue);

        if (error) {
            this.showError(error);
            return;
        }
        this.showError("");
        this.checkButton.disabled = true;

        try {
            const response = await fetch("/fcgi-bin/hello-world.jar", {
                method: "POST",
                headers: {"Content-Type": "application/x-www-form-urlencoded"},
                body: new URLSearchParams({x: xValue.value, y: yText.replace(",", "."), r: rValue.value}).toString()
            });
            const data = await response.json();

            if (!response.ok) {
                this.showError(data.error || "Ошибка сервера");
                return;
            }
            this.storage.save(data);
            this.pageNumberInput.value = "1";
            this.updateResults();
        } catch (e) {
            this.showError("Сервер недоступен");
        } finally {
            this.checkButton.disabled = false;
        }
    }

    showError(message) {
        this.errorMessage.textContent = message;
    }
}
const test = document.getElementById("test");
console.log(test);
document.addEventListener("DOMContentLoaded", () => {new App();});
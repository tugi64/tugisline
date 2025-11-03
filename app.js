// TugisLine CAD Drawing Program
// Main Application Logic

class CADApp {
    constructor() {
        this.canvas = document.getElementById('canvas');
        this.ctx = this.canvas.getContext('2d');
        
        // Drawing state
        this.objects = [];
        this.selectedObject = null;
        this.currentTool = 'select';
        this.isDrawing = false;
        this.startPoint = null;
        this.tempPoints = [];
        
        // View state
        this.panOffset = { x: 0, y: 0 };
        this.zoom = 1.0;
        this.isPanning = false;
        this.lastPanPoint = null;
        
        // Grid settings
        this.gridSize = 20;
        this.showGrid = true;
        this.snapToGrid = true;
        
        // Drawing properties
        this.currentProperties = {
            color: '#000000',
            lineWidth: 2,
            fill: false,
            fillColor: '#cccccc'
        };
        
        this.init();
    }
    
    init() {
        this.setupCanvas();
        this.setupEventListeners();
        this.setupKeyboardShortcuts();
        this.render();
        this.updateStatus('Hazır - Çizime başlamak için bir araç seçin');
    }
    
    setupCanvas() {
        const container = this.canvas.parentElement;
        this.canvas.width = container.clientWidth;
        this.canvas.height = container.clientHeight;
        
        window.addEventListener('resize', () => {
            this.canvas.width = container.clientWidth;
            this.canvas.height = container.clientHeight;
            this.render();
        });
    }
    
    setupEventListeners() {
        // Tool buttons
        document.getElementById('tool-select').addEventListener('click', () => this.setTool('select'));
        document.getElementById('tool-line').addEventListener('click', () => this.setTool('line'));
        document.getElementById('tool-rectangle').addEventListener('click', () => this.setTool('rectangle'));
        document.getElementById('tool-circle').addEventListener('click', () => this.setTool('circle'));
        document.getElementById('tool-polygon').addEventListener('click', () => this.setTool('polygon'));
        
        // Action buttons
        document.getElementById('btn-delete').addEventListener('click', () => this.deleteSelected());
        document.getElementById('btn-clear').addEventListener('click', () => this.clearAll());
        document.getElementById('btn-zoom-in').addEventListener('click', () => this.zoomIn());
        document.getElementById('btn-zoom-out').addEventListener('click', () => this.zoomOut());
        document.getElementById('btn-zoom-reset').addEventListener('click', () => this.zoomReset());
        document.getElementById('btn-save').addEventListener('click', () => this.saveDrawing());
        document.getElementById('btn-load').addEventListener('click', () => this.loadDrawing());
        
        // Canvas events
        this.canvas.addEventListener('mousedown', (e) => this.handleMouseDown(e));
        this.canvas.addEventListener('mousemove', (e) => this.handleMouseMove(e));
        this.canvas.addEventListener('mouseup', (e) => this.handleMouseUp(e));
        this.canvas.addEventListener('wheel', (e) => this.handleWheel(e));
        this.canvas.addEventListener('dblclick', (e) => this.handleDoubleClick(e));
        
        // Property controls
        document.getElementById('prop-color').addEventListener('change', (e) => {
            this.currentProperties.color = e.target.value;
            this.updateSelectedObject();
        });
        
        document.getElementById('prop-linewidth').addEventListener('input', (e) => {
            this.currentProperties.lineWidth = parseInt(e.target.value);
            document.getElementById('linewidth-value').textContent = e.target.value;
            this.updateSelectedObject();
        });
        
        document.getElementById('prop-fill').addEventListener('change', (e) => {
            this.currentProperties.fill = e.target.checked;
            this.updateSelectedObject();
        });
        
        document.getElementById('prop-fillcolor').addEventListener('change', (e) => {
            this.currentProperties.fillColor = e.target.value;
            this.updateSelectedObject();
        });
        
        document.getElementById('toggle-grid').addEventListener('change', (e) => {
            this.showGrid = e.target.checked;
            this.render();
        });
        
        document.getElementById('toggle-snap').addEventListener('change', (e) => {
            this.snapToGrid = e.target.checked;
        });
        
        // File input
        document.getElementById('file-input').addEventListener('change', (e) => this.handleFileLoad(e));
    }
    
    setupKeyboardShortcuts() {
        document.addEventListener('keydown', (e) => {
            if (e.key === 's' || e.key === 'S') this.setTool('select');
            if (e.key === 'l' || e.key === 'L') this.setTool('line');
            if (e.key === 'r' || e.key === 'R') this.setTool('rectangle');
            if (e.key === 'c' || e.key === 'C') this.setTool('circle');
            if (e.key === 'p' || e.key === 'P') this.setTool('polygon');
            if (e.key === 'Delete') this.deleteSelected();
            if (e.key === '+' || e.key === '=') this.zoomIn();
            if (e.key === '-' || e.key === '_') this.zoomOut();
            if (e.key === '0') this.zoomReset();
            if (e.key === 'Escape') {
                this.cancelDrawing();
            }
        });
    }
    
    setTool(tool) {
        this.currentTool = tool;
        this.cancelDrawing();
        
        // Update UI
        document.querySelectorAll('.tool-btn').forEach(btn => btn.classList.remove('active'));
        document.getElementById(`tool-${tool}`).classList.add('active');
        
        // Update cursor
        this.canvas.style.cursor = tool === 'select' ? 'default' : 'crosshair';
        
        this.updateStatus(`Araç: ${tool}`);
    }
    
    getMousePos(e) {
        const rect = this.canvas.getBoundingClientRect();
        let x = (e.clientX - rect.left - this.panOffset.x) / this.zoom;
        let y = (e.clientY - rect.top - this.panOffset.y) / this.zoom;
        
        if (this.snapToGrid) {
            x = Math.round(x / this.gridSize) * this.gridSize;
            y = Math.round(y / this.gridSize) * this.gridSize;
        }
        
        return { x, y };
    }
    
    handleMouseDown(e) {
        const pos = this.getMousePos(e);
        
        // Middle button or space + left button for panning
        if (e.button === 1 || (e.button === 0 && e.shiftKey)) {
            this.isPanning = true;
            this.lastPanPoint = { x: e.clientX, y: e.clientY };
            this.canvas.style.cursor = 'grab';
            return;
        }
        
        if (this.currentTool === 'select') {
            this.selectObject(pos);
        } else if (this.currentTool === 'polygon') {
            this.tempPoints.push(pos);
            this.isDrawing = true;
        } else {
            this.startPoint = pos;
            this.isDrawing = true;
        }
    }
    
    handleMouseMove(e) {
        const pos = this.getMousePos(e);
        
        // Update coordinates display
        document.getElementById('coordinates').textContent = `X: ${Math.round(pos.x)}, Y: ${Math.round(pos.y)}`;
        
        if (this.isPanning && this.lastPanPoint) {
            const dx = e.clientX - this.lastPanPoint.x;
            const dy = e.clientY - this.lastPanPoint.y;
            this.panOffset.x += dx;
            this.panOffset.y += dy;
            this.lastPanPoint = { x: e.clientX, y: e.clientY };
            this.render();
            return;
        }
        
        if (this.isDrawing) {
            this.render();
            this.drawPreview(pos);
        }
    }
    
    handleMouseUp(e) {
        if (this.isPanning) {
            this.isPanning = false;
            this.lastPanPoint = null;
            this.canvas.style.cursor = this.currentTool === 'select' ? 'default' : 'crosshair';
            return;
        }
        
        if (!this.isDrawing || this.currentTool === 'polygon') return;
        
        const pos = this.getMousePos(e);
        this.createObject(pos);
        this.isDrawing = false;
        this.startPoint = null;
    }
    
    handleDoubleClick(e) {
        if (this.currentTool === 'polygon' && this.tempPoints.length >= 3) {
            this.objects.push({
                type: 'polygon',
                points: [...this.tempPoints],
                ...this.currentProperties
            });
            this.tempPoints = [];
            this.isDrawing = false;
            this.render();
            this.updateObjectCount();
        }
    }
    
    handleWheel(e) {
        e.preventDefault();
        const delta = e.deltaY > 0 ? 0.9 : 1.1;
        this.zoom *= delta;
        this.zoom = Math.max(0.1, Math.min(5, this.zoom));
        this.render();
        this.updateZoomLevel();
    }
    
    drawPreview(currentPos) {
        this.ctx.save();
        this.ctx.strokeStyle = this.currentProperties.color;
        this.ctx.lineWidth = this.currentProperties.lineWidth;
        this.ctx.fillStyle = this.currentProperties.fillColor;
        this.ctx.setLineDash([5, 5]);
        
        if (this.currentTool === 'line' && this.startPoint) {
            this.ctx.beginPath();
            this.ctx.moveTo(this.startPoint.x, this.startPoint.y);
            this.ctx.lineTo(currentPos.x, currentPos.y);
            this.ctx.stroke();
        } else if (this.currentTool === 'rectangle' && this.startPoint) {
            const width = currentPos.x - this.startPoint.x;
            const height = currentPos.y - this.startPoint.y;
            if (this.currentProperties.fill) {
                this.ctx.fillRect(this.startPoint.x, this.startPoint.y, width, height);
            }
            this.ctx.strokeRect(this.startPoint.x, this.startPoint.y, width, height);
        } else if (this.currentTool === 'circle' && this.startPoint) {
            const radius = Math.sqrt(
                Math.pow(currentPos.x - this.startPoint.x, 2) +
                Math.pow(currentPos.y - this.startPoint.y, 2)
            );
            this.ctx.beginPath();
            this.ctx.arc(this.startPoint.x, this.startPoint.y, radius, 0, 2 * Math.PI);
            if (this.currentProperties.fill) {
                this.ctx.fill();
            }
            this.ctx.stroke();
        } else if (this.currentTool === 'polygon' && this.tempPoints.length > 0) {
            this.ctx.beginPath();
            this.ctx.moveTo(this.tempPoints[0].x, this.tempPoints[0].y);
            for (let i = 1; i < this.tempPoints.length; i++) {
                this.ctx.lineTo(this.tempPoints[i].x, this.tempPoints[i].y);
            }
            this.ctx.lineTo(currentPos.x, currentPos.y);
            this.ctx.stroke();
        }
        
        this.ctx.restore();
    }
    
    createObject(endPos) {
        if (!this.startPoint) return;
        
        let object = null;
        
        if (this.currentTool === 'line') {
            object = {
                type: 'line',
                start: { ...this.startPoint },
                end: { ...endPos },
                ...this.currentProperties
            };
        } else if (this.currentTool === 'rectangle') {
            object = {
                type: 'rectangle',
                x: this.startPoint.x,
                y: this.startPoint.y,
                width: endPos.x - this.startPoint.x,
                height: endPos.y - this.startPoint.y,
                ...this.currentProperties
            };
        } else if (this.currentTool === 'circle') {
            const radius = Math.sqrt(
                Math.pow(endPos.x - this.startPoint.x, 2) +
                Math.pow(endPos.y - this.startPoint.y, 2)
            );
            object = {
                type: 'circle',
                center: { ...this.startPoint },
                radius: radius,
                ...this.currentProperties
            };
        }
        
        if (object) {
            this.objects.push(object);
            this.render();
            this.updateObjectCount();
        }
    }
    
    selectObject(pos) {
        this.selectedObject = null;
        
        // Check objects in reverse order (top to bottom)
        for (let i = this.objects.length - 1; i >= 0; i--) {
            const obj = this.objects[i];
            if (this.isPointInObject(pos, obj)) {
                this.selectedObject = obj;
                break;
            }
        }
        
        this.render();
        this.updateSelectedInfo();
    }
    
    isPointInObject(point, obj) {
        const threshold = 5;
        
        if (obj.type === 'line') {
            const dist = this.pointToLineDistance(point, obj.start, obj.end);
            return dist < threshold;
        } else if (obj.type === 'rectangle') {
            return point.x >= obj.x - threshold &&
                   point.x <= obj.x + obj.width + threshold &&
                   point.y >= obj.y - threshold &&
                   point.y <= obj.y + obj.height + threshold;
        } else if (obj.type === 'circle') {
            const dist = Math.sqrt(
                Math.pow(point.x - obj.center.x, 2) +
                Math.pow(point.y - obj.center.y, 2)
            );
            return Math.abs(dist - obj.radius) < threshold;
        } else if (obj.type === 'polygon') {
            return this.isPointInPolygon(point, obj.points);
        }
        
        return false;
    }
    
    pointToLineDistance(point, lineStart, lineEnd) {
        const A = point.x - lineStart.x;
        const B = point.y - lineStart.y;
        const C = lineEnd.x - lineStart.x;
        const D = lineEnd.y - lineStart.y;
        
        const dot = A * C + B * D;
        const lenSq = C * C + D * D;
        let param = -1;
        
        if (lenSq !== 0) param = dot / lenSq;
        
        let xx, yy;
        
        if (param < 0) {
            xx = lineStart.x;
            yy = lineStart.y;
        } else if (param > 1) {
            xx = lineEnd.x;
            yy = lineEnd.y;
        } else {
            xx = lineStart.x + param * C;
            yy = lineStart.y + param * D;
        }
        
        const dx = point.x - xx;
        const dy = point.y - yy;
        return Math.sqrt(dx * dx + dy * dy);
    }
    
    isPointInPolygon(point, vertices) {
        let inside = false;
        for (let i = 0, j = vertices.length - 1; i < vertices.length; j = i++) {
            const xi = vertices[i].x, yi = vertices[i].y;
            const xj = vertices[j].x, yj = vertices[j].y;
            
            const intersect = ((yi > point.y) !== (yj > point.y))
                && (point.x < (xj - xi) * (point.y - yi) / (yj - yi) + xi);
            if (intersect) inside = !inside;
        }
        return inside;
    }
    
    deleteSelected() {
        if (this.selectedObject) {
            const index = this.objects.indexOf(this.selectedObject);
            if (index > -1) {
                this.objects.splice(index, 1);
                this.selectedObject = null;
                this.render();
                this.updateObjectCount();
                this.updateSelectedInfo();
            }
        }
    }
    
    clearAll() {
        if (confirm('Tüm nesneleri silmek istediğinizden emin misiniz?')) {
            this.objects = [];
            this.selectedObject = null;
            this.render();
            this.updateObjectCount();
            this.updateSelectedInfo();
        }
    }
    
    cancelDrawing() {
        this.isDrawing = false;
        this.startPoint = null;
        this.tempPoints = [];
        this.render();
    }
    
    updateSelectedObject() {
        if (this.selectedObject) {
            Object.assign(this.selectedObject, this.currentProperties);
            this.render();
        }
    }
    
    zoomIn() {
        this.zoom *= 1.2;
        this.zoom = Math.min(5, this.zoom);
        this.render();
        this.updateZoomLevel();
    }
    
    zoomOut() {
        this.zoom *= 0.8;
        this.zoom = Math.max(0.1, this.zoom);
        this.render();
        this.updateZoomLevel();
    }
    
    zoomReset() {
        this.zoom = 1.0;
        this.panOffset = { x: 0, y: 0 };
        this.render();
        this.updateZoomLevel();
    }
    
    render() {
        // Clear canvas
        this.ctx.clearRect(0, 0, this.canvas.width, this.canvas.height);
        
        this.ctx.save();
        this.ctx.translate(this.panOffset.x, this.panOffset.y);
        this.ctx.scale(this.zoom, this.zoom);
        
        // Draw grid
        if (this.showGrid) {
            this.drawGrid();
        }
        
        // Draw all objects
        for (const obj of this.objects) {
            this.drawObject(obj, obj === this.selectedObject);
        }
        
        this.ctx.restore();
    }
    
    drawGrid() {
        this.ctx.save();
        this.ctx.strokeStyle = '#e0e0e0';
        this.ctx.lineWidth = 1 / this.zoom;
        
        const startX = -this.panOffset.x / this.zoom;
        const startY = -this.panOffset.y / this.zoom;
        const endX = startX + this.canvas.width / this.zoom;
        const endY = startY + this.canvas.height / this.zoom;
        
        for (let x = Math.floor(startX / this.gridSize) * this.gridSize; x < endX; x += this.gridSize) {
            this.ctx.beginPath();
            this.ctx.moveTo(x, startY);
            this.ctx.lineTo(x, endY);
            this.ctx.stroke();
        }
        
        for (let y = Math.floor(startY / this.gridSize) * this.gridSize; y < endY; y += this.gridSize) {
            this.ctx.beginPath();
            this.ctx.moveTo(startX, y);
            this.ctx.lineTo(endX, y);
            this.ctx.stroke();
        }
        
        this.ctx.restore();
    }
    
    drawObject(obj, selected = false) {
        this.ctx.save();
        this.ctx.strokeStyle = obj.color;
        this.ctx.lineWidth = obj.lineWidth;
        this.ctx.fillStyle = obj.fillColor;
        
        if (selected) {
            this.ctx.strokeStyle = '#3498db';
            this.ctx.lineWidth = obj.lineWidth + 2;
            this.ctx.setLineDash([10, 5]);
        }
        
        if (obj.type === 'line') {
            this.ctx.beginPath();
            this.ctx.moveTo(obj.start.x, obj.start.y);
            this.ctx.lineTo(obj.end.x, obj.end.y);
            this.ctx.stroke();
        } else if (obj.type === 'rectangle') {
            if (obj.fill) {
                this.ctx.fillRect(obj.x, obj.y, obj.width, obj.height);
            }
            this.ctx.strokeRect(obj.x, obj.y, obj.width, obj.height);
        } else if (obj.type === 'circle') {
            this.ctx.beginPath();
            this.ctx.arc(obj.center.x, obj.center.y, obj.radius, 0, 2 * Math.PI);
            if (obj.fill) {
                this.ctx.fill();
            }
            this.ctx.stroke();
        } else if (obj.type === 'polygon') {
            this.ctx.beginPath();
            this.ctx.moveTo(obj.points[0].x, obj.points[0].y);
            for (let i = 1; i < obj.points.length; i++) {
                this.ctx.lineTo(obj.points[i].x, obj.points[i].y);
            }
            this.ctx.closePath();
            if (obj.fill) {
                this.ctx.fill();
            }
            this.ctx.stroke();
        }
        
        this.ctx.restore();
    }
    
    saveDrawing() {
        const data = {
            version: '1.0',
            objects: this.objects,
            zoom: this.zoom,
            panOffset: this.panOffset
        };
        
        const json = JSON.stringify(data, null, 2);
        const blob = new Blob([json], { type: 'application/json' });
        const url = URL.createObjectURL(blob);
        
        const a = document.createElement('a');
        a.href = url;
        a.download = `tugisline-${Date.now()}.json`;
        a.click();
        
        URL.revokeObjectURL(url);
        this.updateStatus('Çizim kaydedildi');
    }
    
    loadDrawing() {
        document.getElementById('file-input').click();
    }
    
    handleFileLoad(e) {
        const file = e.target.files[0];
        if (!file) return;
        
        const reader = new FileReader();
        reader.onload = (event) => {
            try {
                const data = JSON.parse(event.target.result);
                this.objects = data.objects || [];
                this.zoom = data.zoom || 1.0;
                this.panOffset = data.panOffset || { x: 0, y: 0 };
                this.render();
                this.updateObjectCount();
                this.updateZoomLevel();
                this.updateStatus('Çizim yüklendi');
            } catch (error) {
                alert('Dosya yüklenirken hata oluştu: ' + error.message);
            }
        };
        reader.readAsText(file);
        e.target.value = '';
    }
    
    updateStatus(text) {
        document.getElementById('status-text').textContent = text;
    }
    
    updateObjectCount() {
        document.getElementById('object-count').textContent = `Nesneler: ${this.objects.length}`;
    }
    
    updateZoomLevel() {
        document.getElementById('zoom-level').textContent = `Yakınlaştırma: ${Math.round(this.zoom * 100)}%`;
    }
    
    updateSelectedInfo() {
        const infoDiv = document.getElementById('selected-info');
        const detailsDiv = document.getElementById('selected-details');
        
        if (this.selectedObject) {
            infoDiv.style.display = 'block';
            let details = `<p>Tip: ${this.selectedObject.type}</p>`;
            details += `<p>Renk: ${this.selectedObject.color}</p>`;
            details += `<p>Kalınlık: ${this.selectedObject.lineWidth}px</p>`;
            details += `<p>Dolu: ${this.selectedObject.fill ? 'Evet' : 'Hayır'}</p>`;
            detailsDiv.innerHTML = details;
            
            // Update property controls
            document.getElementById('prop-color').value = this.selectedObject.color;
            document.getElementById('prop-linewidth').value = this.selectedObject.lineWidth;
            document.getElementById('linewidth-value').textContent = this.selectedObject.lineWidth;
            document.getElementById('prop-fill').checked = this.selectedObject.fill;
            document.getElementById('prop-fillcolor').value = this.selectedObject.fillColor;
        } else {
            infoDiv.style.display = 'none';
        }
    }
}

// Initialize the application when DOM is ready
document.addEventListener('DOMContentLoaded', () => {
    new CADApp();
});

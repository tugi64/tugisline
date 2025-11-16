# PowerShell commit & push script
$ErrorActionPreference = 'Stop'

# Repo kök dizinine geç
Set-Location -Path (Split-Path -Parent $MyInvocation.MyCommand.Path)

# Stage all
& git add -A

# Çok satırlı commit mesajı için geçici dosya kullan
$tmp = New-TemporaryFile
@'
feat: Advanced CAD features - Drawing tools, Layer management & Enhanced UI

✅ Drawing Tools (Çizim Araçları):
- LINE: Click and drag to draw lines
- RECTANGLE: Draw rectangles with real-time preview
- CIRCLE: Create circles with radius control
- ARC & TEXT: Prepared for future implementation
- Real-time shape preview while drawing
- All drawn shapes persist on canvas

✅ Layer Management (Katman Yönetimi):
- Edit layer dialog with name, line type, and weight
- Layer color display and management
- Visual feedback for all layer states
- Freeze/Lock/Hide individual layers
- Enhanced layer card UI

✅ Main Menu Improvements:
- Smart file filtering by tabs (Recent/Favorites/Local)
- Sorting: Recent by date, Favorites filtered, Local alphabetically
- New Project dialog fully functional
- Enhanced file search with case-insensitive matching

✅ Settings Screen:
- Reset to defaults functionality
- Reset dialog with confirmation
- All settings properly saved in state
- Reset button in top bar

✅ Viewer Enhancements:
- Precise coordinate display (2 decimal places)
- Drawing mode vs Pan mode separation
- onDragStart/onDrag/onDragEnd event handling
- Tool-specific cursor behavior
- Zoom limits enforced (0.1x - 10x)

✅ Navigation:
- FileExplorer route added
- All navigation paths working
- Proper back navigation

Technical Improvements:
- DrawnShape data model with ShapeType enum
- State management for drawing tools
- Canvas gesture conflict resolution
- Remember-based state for all screens
- Proper Compose lifecycle handling

UI/UX:
- Consistent Material3 design
- Color-coded visual feedback
- Smooth animations and transitions
- Responsive touch controls
'@ | Set-Content -Path $tmp -Encoding UTF8

& git commit -F $tmp.FullName
Remove-Item $tmp -Force

# Push
$branch = $env:BRANCH
if ([string]::IsNullOrEmpty($branch)) { $branch = 'main' }
& git push -u origin $branch


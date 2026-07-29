import os
import glob
import re

base_dir = r'C:\Universida Israel\6to Semestre\Desarrollo de software\Code\HotelFront1-main\src\main\resources\templates'

# Pattern to find the <button>...</button> that contains &times;
# Since it can span multiple lines, we use a dotall regex
pattern = re.compile(r'<button[^>]*?>\s*(?:<span[^>]*>)?&times;(?:</span>)?\s*</button>', re.IGNORECASE | re.DOTALL)

for filepath in glob.glob(os.path.join(base_dir, '**', '*.html'), recursive=True):
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()
    
    def replacer(match):
        text = match.group(0)
        # Keep it if it's an alert dismiss button
        if 'data-dismiss="alert"' in text or "data-dismiss='alert'" in text:
            return text
        return ''
        
    new_content = pattern.sub(replacer, content)
    
    if new_content != content:
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write(new_content)
        print(f'Modified {filepath}')

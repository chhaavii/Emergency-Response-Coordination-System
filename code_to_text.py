import os
import sys
from pathlib import Path

def get_source_files(directory, extensions=None):
    if extensions is None:
        extensions = {'.py', '.java', '.js', '.html', '.css', '.cpp', '.c', '.h', '.hpp', '.cs', '.go', '.rb', '.php', '.swift', '.kt'}
    
    source_files = []
    for root, _, files in os.walk(directory):
        for file in files:
            if Path(file).suffix.lower() in extensions:
                source_files.append(os.path.join(root, file))
    return source_files

def read_file_content(file_path):
    try:
        with open(file_path, 'r', encoding='utf-8') as file:
            return file.read()
    except Exception as e:
        return f"Error reading {file_path}: {str(e)}\n"

def write_to_output(directory, output_file='all_codes.txt'):
    output_path = os.path.join(directory, output_file)
    source_files = get_source_files(directory)
    
    with open(output_path, 'w', encoding='utf-8') as outfile:
        for file_path in source_files:
            rel_path = os.path.relpath(file_path, directory)
            outfile.write(f"\n{'='*80}\n")
            outfile.write(f"File: {rel_path}\n")
            outfile.write(f"{'='*80}\n\n")
            
            content = read_file_content(file_path)
            outfile.write(content)
            outfile.write("\n\n")
    
    print(f"Successfully wrote {len(source_files)} source files to {output_path}")

if __name__ == "__main__":
    # Use the current directory if no directory is specified
    target_dir = sys.argv[1] if len(sys.argv) > 1 else os.getcwd()
    write_to_output(target_dir)

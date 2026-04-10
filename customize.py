#!/usr/bin/env python3
"""
Замена ФИО и цвета кнопок во всех лабораторных работах.

Использование:
    python3 customize.py "Фамилия Имя Отчество"
    python3 customize.py "Фамилия Имя Отчество" --color "#42A5F5"

Скрипт автоматически определяет текущее ФИО из файлов проекта.
"""

import sys
import os
import re
import random

ROOT = os.path.dirname(os.path.abspath(__file__))

# 10 приятных оттенков, хорошо видных на светлом фоне Material3
COLORS = [
    "#5C6BC0",  # Indigo
    "#7E57C2",  # Deep Purple
    "#26A69A",  # Teal
    "#1E88E5",  # Blue
    "#43A047",  # Green
    "#E91E8C",  # Pink
    "#F4511E",  # Deep Orange
    "#8E24AA",  # Purple
    "#00ACC1",  # Cyan
    "#FB8C00",  # Orange
]


def detect_current_name() -> str | None:
    """Ищет текущее ФИО в strings.xml или layout-файлах лабораторных работ."""
    for i in range(1, 9):
        strings_path = os.path.join(
            ROOT, f"lb{i}", "app", "src", "main", "res", "values", "strings.xml"
        )
        if os.path.exists(strings_path):
            with open(strings_path, encoding="utf-8") as f:
                content = f.read()
            m = re.search(r'<string name="student_name">([^<]+)</string>', content)
            if m:
                return m.group(1)

    # Резерв: прямой текст в layout (lb1, lb2)
    for i in range(1, 9):
        layout_path = os.path.join(
            ROOT, f"lb{i}", "app", "src", "main", "res", "layout", "activity_main.xml"
        )
        if os.path.exists(layout_path):
            with open(layout_path, encoding="utf-8") as f:
                content = f.read()
            m = re.search(r'<TextView[^>]*android:id="@\+id/tv_name"[^>]*>', content, re.DOTALL)
            if m:
                nm = re.search(r'android:text="([^"@][^"]*)"', m.group(0))
                if nm:
                    return nm.group(1)
    return None


def replace_in_file(path: str, old: str, new: str) -> bool:
    with open(path, encoding="utf-8") as f:
        content = f.read()
    if old not in content:
        return False
    with open(path, "w", encoding="utf-8") as f:
        f.write(content.replace(old, new))
    return True


def update_colors_xml(path: str, color: str) -> None:
    with open(path, encoding="utf-8") as f:
        content = f.read()

    entry = f'    <color name="button_color">{color}</color>'
    if 'name="button_color"' in content:
        content = re.sub(
            r'[ \t]*<color name="button_color">[^<]*</color>',
            f'    <color name="button_color">{color}</color>',
            content,
        )
    else:
        content = content.replace("</resources>", f"{entry}\n</resources>")

    with open(path, "w", encoding="utf-8") as f:
        f.write(content)


def add_tint_to_buttons(path: str) -> bool:
    """Добавляет app:backgroundTint="@color/button_color" ко всем <Button> в layout."""
    with open(path, encoding="utf-8") as f:
        content = f.read()

    if "<Button" not in content:
        return False

    def patch_button(m: re.Match) -> str:
        tag = m.group(0)
        if "backgroundTint" in tag:
            # Обновить существующий атрибут
            tag = re.sub(
                r'app:backgroundTint="[^"]*"',
                'app:backgroundTint="@color/button_color"',
                tag,
            )
        else:
            # Вставить перед закрывающим />
            tag = re.sub(r"\s*/>$", '\n        app:backgroundTint="@color/button_color" />', tag)
        return tag

    new_content = re.sub(r"<Button\b[^>]*/\s*>", patch_button, content, flags=re.DOTALL)

    if new_content == content:
        return False

    with open(path, "w", encoding="utf-8") as f:
        f.write(new_content)
    return True


def main() -> None:
    # --- Аргументы ---
    args = sys.argv[1:]
    if not args or args[0].startswith("-"):
        print("Использование: python3 customize.py \"Фамилия Имя Отчество\" [--color #RRGGBB]")
        sys.exit(1)

    new_name = args[0]

    chosen_color = None
    if "--color" in args:
        idx = args.index("--color")
        if idx + 1 < len(args):
            chosen_color = args[idx + 1]
        else:
            print("Ошибка: после --color нужно указать цвет, например #42A5F5")
            sys.exit(1)

    if chosen_color is None:
        chosen_color = random.choice(COLORS)

    # --- Текущее ФИО ---
    old_name = detect_current_name()
    if old_name is None:
        print("Не удалось определить текущее ФИО. Убедитесь, что структура проекта не изменена.")
        sys.exit(1)

    if old_name == new_name:
        print(f"ФИО уже установлено как «{new_name}», замена не нужна.")
    else:
        print(f"Текущее ФИО : {old_name}")
        print(f"Новое ФИО   : {new_name}")

    print(f"Цвет кнопок : {chosen_color}")
    print()

    labs = sorted(
        d for d in os.listdir(ROOT)
        if re.match(r"^lb\d+$", d) and os.path.isdir(os.path.join(ROOT, d))
    )

    for lab in labs:
        src_main = os.path.join(ROOT, lab, "app", "src", "main")
        if not os.path.exists(src_main):
            continue

        changed = []

        # Замена ФИО во всех XML и Java-файлах src/main
        if old_name != new_name:
            for dirpath, _, filenames in os.walk(src_main):
                for filename in filenames:
                    if filename.endswith((".xml", ".java")):
                        fp = os.path.join(dirpath, filename)
                        if replace_in_file(fp, old_name, new_name):
                            changed.append(f"  ФИО → {os.path.relpath(fp, ROOT)}")

        # Обновление colors.xml
        colors_xml = os.path.join(src_main, "res", "values", "colors.xml")
        if os.path.exists(colors_xml):
            update_colors_xml(colors_xml, chosen_color)
            changed.append(f"  Цвет → {os.path.relpath(colors_xml, ROOT)}")

        # Добавление backgroundTint к кнопкам в layout
        layout_dir = os.path.join(src_main, "res", "layout")
        if os.path.exists(layout_dir):
            for filename in os.listdir(layout_dir):
                if filename.endswith(".xml"):
                    fp = os.path.join(layout_dir, filename)
                    if add_tint_to_buttons(fp):
                        changed.append(f"  Кнопки → {os.path.relpath(fp, ROOT)}")

        if changed:
            print(f"[{lab}]")
            print("\n".join(changed))
        else:
            print(f"[{lab}] — без изменений")

    print()
    print("Готово. Откройте Android Studio, чтобы увидеть изменения.")
    print(f"Совет: если кнопки выглядят темно, убедитесь, что тема не переопределяет colorPrimary.")


if __name__ == "__main__":
    main()

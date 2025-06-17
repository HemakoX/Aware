import xml.etree.ElementTree as ET
from googletrans import Translator
import os


def format_key_to_english_value(key):
    parts = key.split('_')
    if len(parts) == 0:
        return key
    # Capitalize first word only, rest lowercase
    return parts[0].capitalize() + ' ' + ' '.join(parts[1:])


def add_strings_to_xml(tree, new_strings):
    root = tree.getroot()
    existing_keys = {elem.attrib['name'] for elem in root.findall('string')}

    for key, val in new_strings.items():
        if key not in existing_keys:
            new_elem = ET.Element('string', name=key)
            new_elem.text = val
            root.append(new_elem)


def translate_strings(strings_dict, dest_lang='ar'):
    translator = Translator()
    translations = {}
    for k, v in strings_dict.items():
        translated_text = translator.translate(v, dest=dest_lang).text
        translations[k] = translated_text
    return translations


def prettify(elem, level=0):
    # Add indentation for pretty xml output
    indent = "    "
    i = "\n" + level * indent
    if len(elem):
        if not elem.text or not elem.text.strip():
            elem.text = i + indent
        for e in elem:
            prettify(e, level + 1)
        if not e.tail or not e.tail.strip():
            e.tail = i
    else:
        if level and (not elem.tail or not elem.tail.strip()):
            elem.tail = i


def main():
    keys = [
        "apply_changes",
    ]

    # Format keys to English values
    eng_strings = {k: format_key_to_english_value(k) for k in keys}

    # Load or create English strings.xml
    if os.path.exists('values/strings.xml'):
        eng_tree = ET.parse('values/strings.xml')
    else:
        eng_tree = ET.ElementTree(ET.Element('resources'))

    add_strings_to_xml(eng_tree, eng_strings)
    prettify(eng_tree.getroot())
    eng_tree.write('values/strings.xml', encoding='utf-8', xml_declaration=True)
    print("Updated English strings.xml")

    # Translate to Arabic
    ar_strings = translate_strings(eng_strings, 'ar')

    # Load or create Arabic strings.xml
    if os.path.exists('values-ar/strings.xml'):
        ar_tree = ET.parse('values-ar/strings.xml')
    else:
        ar_tree = ET.ElementTree(ET.Element('resources'))

    add_strings_to_xml(ar_tree, ar_strings)
    prettify(ar_tree.getroot())
    os.makedirs('values-ar', exist_ok=True)
    ar_tree.write('values-ar/strings.xml', encoding='utf-8', xml_declaration=True)
    print("Created/Updated Arabic strings.xml")


if __name__ == "__main__":
    main()

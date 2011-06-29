#/usr/bin/python!
# -*- coding: utf-8 -*-

"""
/** 
 ** Copyright (c) 2011 Ushahidi Inc
 ** All rights reserved
 ** Contact: team@ushahidi.com
 ** Website: http://www.ushahidi.com
 ** 
 ** GNU Lesser General Public License Usage
 ** This file may be used under the terms of the GNU Lesser
 ** General Public License version 3 as published by the Free Software
 ** Foundation and appearing in the file LICENSE.LGPL included in the
 ** packaging of this file. Please review the following information to
 ** ensure the GNU Lesser General Public License version 3 requirements
 ** will be met: http://www.gnu.org/licenses/lgpl.html.	
 **	
 **
 ** If you have questions regarding the use of this file, please contact
 ** Ushahidi developers at team@ushahidi.com.
 ** 
 **/ 

This class generates string.xml for Android from a csv file and as well generates string.xml from 
csv file for Android.

Usage: python stringgen.py [options] [source] [destination]

Options:
  -i ..., --import        use to specify weather it should import from csv file 
  -l ..., --language=...  use to specify the langauge to export from
  -h, --help              show this help
  -d                      show debugging information while parsing

Examples:
  stringen.py                            prints usage info
  stringen.py -il en csv.txt string.xml   generates string.xml from csv file 
  stringen.py string.xml csv.txt     reads from string.xml and generates csv file

"""

__author__ = "Henry Addo (henry@ushahidi.com)"
__version__ = "$Revision: 1.0 $"
__date__ = "$Date: 2011/06/17 17:17:19 $"
__copyright__ = "Copyright (c) 2011 Ushahidi"
__license__ = "GPL"

import xml.dom.minidom
import csv
import os
import sys

from optparse import OptionParser

class StringGenerator:
    
    """ generate string.xml or csv.txt file """
    
    def __init__(self,lang,source, destination):
        self.languages= {"ab":"Abkhaz","aa":"Afar","af":"Afrikaans ",
        "ak" :"Akan ",
        "sq" :"Albanian ",
        "am" :"Amharic ",
        "ar" :"Arabic ",
        "an" :"Aragonese ",
        "hy" :"Armenian ",
        "as" :"Assamese ",
        "av" :"Avaric ",
        "ae" :"Avestan ",
        "ay" :"Aymara ",
        "az" :"Azerbaijani ",
        "bm" :"Bambara ",
        "ba" :"Bashkir ",
        "eu" :"Basque ",
        "be" :"Belarusian ",
        "bn" :"Bengali ",
        "bh" :"Bihari ",
        "bi" :"Bislama ",
        "bs" :"Bosnian ",
        "br" :"Breton ",
        "bg" :"Bulgarian ",
        "my" :"Burmese ",
        "ca" :"Catalan; Valencian ",
        "ch" :"Chamorro ",
        "ce" :"Chechen ",
        "ny" :"Chichewa; Chewa; Nyanja ",
        "zh" :"Chinese ",
        "cv" :"Chuvash ",
        "kw" :"Cornish ",
        "co" :"Corsican ",
        "cr" :"Cree ",
        "hr" :"Croatian ",
        "cs" :"Czech ",
        "da" :"Danish ",
        "dv" :"Divehi; Dhivehi; Maldivian; ",
        "nl" :"Dutch ",
        "en" :"English ",
        "eo" :"Esperanto ",
        "et" :"Estonian ",
        "ee" :"Ewe ",
        "fo" :"Faroese ",
        "fj" :"Fijian ",
        "fi" :"Finnish ",
         "suomi, suomen kieli "
        "fr" :"French ",
        "ff" :"Fula; Fulah; Pulaar; Pular ",
        "gl" :"Galician ",
        "ka" :"Georgian ",
        "de" :"German ",
        "el" :"Greek, Modern ",
        "gn" :"Guaraní ",
        "gu" :"Gujarati ",
        "ht" :"Haitian; Haitian Creole ",
        "ha" :"Hausa ",
        "he" :"Hebrew",
        "hz" :"Herero ",
        "hi" :"Hindi ",
        "ho" :"Hiri Motu ",
        "hu" :"Hungarian ",
        "ia" :"Interlingua ",
        "id" :"Indonesian ",
        "ie" :"Interlingue ",
        "ga" :"Irish ",
        "ig" :"Igbo ",
        "ik" :"Inupiaq ",
        "io" :"Ido ",
        "is" :"Icelandic ",
        "it" :"Italian ",
        "iu" :"Inuktitut ",
        "ja" :"Japanese ",
        "jv" :"Javanese ",
        "kl" :"Kalaallisut, Greenlandic ",
        "kn" :"Kannada ",
        "kr" :"Kanuri ",
        "ks" :"Kashmiri ",
        "kk" :"Kazakh ",
        "km" :"Khmer ",
        "ki" :"Kikuyu, Gikuyu ",
        "rw" :"Kinyarwanda ",
        "ky" :"Kirghiz, Kyrgyz ",
        "kv" :"Komi ",
        "kg" :"Kongo ",
        "ko" :"Korean ",
        "ku" :"Kurdish ",
        "kj" :"Kwanyama, Kuanyama ",
        "la" :"Latin ",
        "lb" :"Luxembourgish, Letzeburgesch ",
        "lg" :"Luganda ",
        "li" :"Limburgish, Limburgan, Limburger ",
        "ln" :"Lingala ",
        "lo" :"Lao ",
        "lt" :"Lithuanian ",
        "lu" :"Luba-Katanga ",
        "lv" :"Latvian ",
        "gv" :"Manx ",
        "mk" :"Macedonian ",
        "mg" :"Malagasy ",
        "ms" :"Malay ",
        "ml" :"Malayalam ",
        "mt" :"Maltese ",
        "mi" :"Māori ",
        "mr" :"Marathi (Marāṭhī) ",
        "mh" :"Marshallese ",
        "mn" :"Mongolian ",
        "na" :"Nauru ",
        "nv" :"Navajo, Navaho ",
        "nb" :"Norwegian Bokmål ",
        "nd" :"North Ndebele ",
        "ne" :"Nepali ",
        "ng" :"Ndonga ",
        "nn" :"Norwegian Nynorsk ",
        "no" :"Norwegian ",
        "ii" :"Nuosu ",
        "nr" :"South Ndebele ",
        "oc" :"Occitan ",
        "oj" :"Ojibwe, Ojibwa ",
        "cu" :
        "Old Church Slavonic, Church Slavic, Church Slavonic, Old Bulgarian, Old Slavonic ",
        "om" :"Oromo ",
        "or" :"Oriya ",
        "os" :"Ossetian, Ossetic ",
        "pa" :"Panjabi, Punjabi ",
        "pi" :"Pāli ",
        "fa" :"Persian ",
        "pl" :"Polish ",
        "ps" :"Pashto, Pushto ",
        "pt" :"Portuguese ",
        "qu" :"Quechua ",
        "rm" :"Romansh ",
        "rn" :"Kirundi ",
        "ro" :"Romanian, Moldavian, Moldovan ",
        "ru" :"Russian ",
        "sa" :"Sanskrit (Saṁskṛta) ",
        "sc" :"Sardinian ",
        "sd" :"Sindhi ",
        "se" :"Northern Sami ",
        "sm" :"Samoan ",
        "sg" :"Sango ",
        "sr" :"Serbian ",
        "gd" :"Scottish Gaelic; Gaelic ",
        "sn" :"Shona ",
        "si" :"Sinhala, Sinhalese ",
        "sk" :"Slovak ",
        "sl" :"Slovene ",
        "so" :"Somali ",
        "st" :"Southern Sotho ",
        "es" :"Spanish; Castilian ",
        "su" :"Sundanese ",
        "sw" :"Swahili ",
        "ss" :"Swati ",
        "sv" :"Swedish ",
        "ta" :"Tamil ",
        "te" :"Telugu ",
        "tg" :"Tajik ",
        "th" :"Thai ",
        "ti" :"Tigrinya ",
        "bo" :"Tibetan Standard, Tibetan, Central ",
        "tk" :"Turkmen ",
        "tl" :"Tagalog ",
        "tn" :"Tswana ",
        "to" :"Tonga (Tonga Islands) ",
        "tr" :"Turkish ",
        "ts" :"Tsonga ",
        "tt" :"Tatar ",
        "tw" :"Twi ",
        "ty" :"Tahitian ",
        "ug" :"Uighur, Uyghur ",
        "uk" :"Ukrainian ",
        "ur" :"Urdu ",
        "uz" :"Uzbek ",
        "ve" :"Venda ",
        "vi" :"Vietnamese ",
        "vo" :"Volapük ",
        "wa" :"Walloon ",
        "cy" :"Welsh ",
        "wo" :"Wolof ",
        "fy" :"Western Frisian ",
        "xh" :"Xhosa ",
        "yi" :"Yiddish ",
        "yo" :"Yoruba ",
        "za" :"Zhuang, Chuang "}
        
        self.source = source
        self.destination = destination
        self.lang = lang
        self.generate_string_xml(lang,source,destination)    

    def generate_string_xml(self,lang,csvfile,destination):
        """ generate strings.xml file from a csv file """
        csv_data = csv.reader(open(csvfile,"rb"))  
        doc = xml.dom.minidom.Document()
        resources = doc.createElementNS(
            "urn:oasis:names:tc:xliff:document:1.2","resources")
        doc.appendChild(resources)
        for row in csv_data:
            if len(row) and len(row) == 2:
                string = doc.createElement("string")
                string.setAttribute("name",row[0])
                string_text = doc.createTextNode(row[1])
                string.appendChild(string_text)
                resources.appendChild(string)

        directory = self.create_dir(lang,destination)
        stringxml = open(directory+"/strings.xml","w")
        stringxml.writelines(doc.toprettyxml(indent="  "))
        stringxml.close()

    #print doc.toprettyxml(indent="  ")             
    #def generate_csv(self):
    
    def create_dir(self, lang, destination):
        """ create android value-lang directory """
        directory = "%s/values-%s" % (destination,lang)
        
        if not os.path.isdir(directory):
            os.makedirs(directory)
        return directory

    def write_xml_to_file(self, doc, destdir="strings.xml"):
        file_obj = open(destdir, "w");
        xml.dom.ext.PrettyPrint(doc, file_obj)
        file_obj.close()
    
    def write_xml_to_screen(self,doc):
        xml.dom.ext.PrettyPrint(doc, file_obj)

# def sync_files(self,oldfile, newflie):

def main(args,options,parser):
    if len(args) < 3:
        parser.print_usage() 
        print "%s: " %(str(len(args)))

    else:
        stringgen = StringGenerator(args[0],args[1],args[2])
#stringgen.create_dir('en',args[1])

        if options.pot == 'pot':
            stringgen.create_dir('en',args[1])

usage = "usage: %prog [options] <language_2_letter_iso> <source_file> <destination_folder>"
parser = OptionParser(usage=usage)
parser.add_option("-i","--import", action="store",
        help="import csv file and generate string.xml file",dest="import")
parser.add_option("-p","--pot",action="store",help="generate pot file from string.xml file", dest="pot")
(options, args) = parser.parse_args()

if __name__ == "__main__":
    main(args, options,parser)


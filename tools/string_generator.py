#/usr/bin/python!
"""
/** 
 ** Copyright (c) 2010 Ushahidi Inc
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

Generates mock philosophy based on a context-free grammar

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

import xml.dom.ext
import xml.dom.minidom
import csv

class StringGenerator:
    
    """ generate string.xml or csv.txt file """
    
    def __init__(self, lang=None, source=None, destion=None):
    
    def generate_string_xml(self,csvfile, destdir):
        """ generate strings.xml file from a csv file """
        data = csv.reader(open(csvfile))  
        doc = xml.dom.minidom.Document()
        
            resources = doc.createElementNS("urn:oasis:names:tc:xliff:document:1.2","resources")

        doc.appendChild(resources)
        for row in data:
            doc.
        return doc


    def generate_csv(self):
    
    def write_xml_to_file(self, doc, destdir="strings.xml"):
        file_obj = open(destdir, "w");
        xml.dom.ext.PrettyPrint(doc, file_obj)
        file_obj.close()
    
    def write_xml_to_screen(self,doc):
        xml.dom.ext.PrettyPrint(doc, file_obj)

    def sync_files(self,oldfile, newflie):


def main(argv):
    string = "string.xml"
    try:
        opts, args = getopt.getopt(argv, "hg:d", ["help", "lang="])
    except getopt.GetoptError:
        usage()
        sys.exit(2)
    for opt, arg in opts:
        if opt in ("-h", "--help"):
            usage()
            sys.exit()
        elif opt == '-d':
            global _debug
            _debug = 1
        elif opt in ("-l", "--lang"):
            string = arg
        elif opt in ("-i", "--import"):
            usage()
    
    source = "".join(args)
    k = KantGenerator(grammar, source)
    print k.output()
oh 
if __name__ == "__main__":
    main(sys.argv[1:])

import sys
import os

from optparse import OptionParser
from lxml import etree

class AndroidString:

    def __init__(self, input_path, output_path):
        self.input_path = input_path
        self.output_path = output_path
        self.dic_gen()

	# print usage instructions.
	def usage(self):
		print ''
		
    #generate dictionary xml file
    def dic_gen(self):
        self.write_xml_doc_to_file( self.read_huns_dict() )
        #self.write_xml_doc()

    #read hunspell dict file
    def read_huns_dict(self):
        f = open( self.input_path, 'r')
        dict_content = f.readlines()
        f.close()

        return dict_content

    def structure_dict_xml(self, huns_dict):
        child_element = ""
        header = '<?xml version="1.0" encoding="UTF-8" ?>\n'
        start_root_element ='<resources xmlns:xliff="urn:oasis:names:tc:xliff:document:1.2">\n'
        for word in huns_dict:
			part_of_child_element = '<string name="'
			splitted_str = word.split("\n\r")
			translated = splitted_str[0].rstrip("\n\r").split(",")
			values_of_child_element = translated[0]+'">'+splitted_str[0].rstrip("\n\r")
			end_of_child_element = '</string>\n'
			child_element += part_of_child_element + values_of_child_element + end_of_child_element 

        end_root_element = '</resources>'

        build_xml_doc = header + start_root_element + child_element + end_root_element

        return build_xml_doc

    #write xml content to file
    def write_xml_doc_to_file(self, huns_dict):
        f = open( self.output_path + '/strings.xml','w')
        f.writelines( self.structure_dict_xml(huns_dict) )
        f.close()

if __name__ == '__main__':
    parser = OptionParser()
    (options, args) = parser.parse_args()
    
    if len(args) > 1:

        dict_generator = DictGenerator(args[0], args[1] )
        
        #print dict_generator.structure_dict_xml()





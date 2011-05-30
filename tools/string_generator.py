#/usr/bin/python!
''' Reads the hunspell dictionary definition for ak-GH
and then generate a dicionary.xml file for android '''

import sys
import os

from optparse import OptionParser

class DictGenerator:

    def __init__(self, input_path, output_path):
        self.input_path = input_path
        self.output_path = output_path
        self.dic_gen()

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

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
import textwrap

from optparse import OptionParser
from xml.dom.minidom import Node

class StringGenerator:
    
    """ generate string.xml or csv.txt file """
    
    def __init__(self,source, destination):
                
        self.source = source
        self.destination = destination
    def generate_string_xml(self,lang):
        """ generate strings.xml file from a csv file """
        csv_data = csv.reader(open(self.source,"rb"))  
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

        directory = self.create_dir(lang,self.destination)
        stringxml = open(directory+"/strings.xml","w")
        stringxml.writelines(doc.toprettyxml(indent="  "))
        stringxml.close()
    
    def write_string_xml_file(self,lang, string_data):
        """ writes content of a list to an Android string.xml file """
        doc = xml.dom.minidom.Document()
        resources = doc.createElementNS(
            "urn:oasis:names:tc:xliff:document:1.2","resources")
        doc.appendChild(resources)
        for row in string_data:
            #if len(row) and len(row) == 2:
            string = doc.createElement("string")
            string.setAttribute("name",row[0])
            string_text = doc.createTextNode(row[1])
            resources.appendChild(string)
        directory = self.create_dir(lang,self.destination)
        stringxml = open(directory+ "/strings.xml","w")
        stringxml.writelines(doc.toprettyxml(indent="  "))
        stringxml.close()

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

    def ReadLines(self, path):
        f = open(path, 'r')
        content = f.readlines()
        f.close()
        
        return content
    
    def write_xml_to_screen(self,doc):
        xml.dom.ext.PrettyPrint(doc, file_obj)

    def generate_pot_from_csv(self,filename):
        """ generate a pot file from csv """
        destfile = self.destination + filename

        """ read csv file """
        csv_data = csv.reader(open(self.source,"rb"))

        if os.path.exists(destfile):
            os.remove(destfile)
        f = open(destfile,'w')
        for row in csv_data:
            
            if len(row) and len(row) == 3:
                
                f.write('#Key: %s\n'% row[0])
                f.write('msgid "%s"\n' % row[1])
                f.write('msgstr "%s"\n\n' % row[2])
        
        f.close();

    def generate_pot_from_csv2(self):
        """ generate a pot file from csv """
        destfile = self.destination
        col = 1
        r = 1
        """ read csv file """
        csv_data = csv.reader(open(self.source,"rb"))

        #if os.path.exists(destfile):
        #    os.remove(destfile)
        f = open(destfile + 'test.po','w')
        for row in csv_data:
            #if r < 197:
            #print row[0]
            print row[1]
            #print row[22]
            #r +=1
            #if col < 28:
                #f = open(destfile + 'test.po','w')
                #f.write ('#key: %s\n'% row[col])
             #   for data in row: 
              #      f.write ('#key: %s\n'% csv_data[0])
                    #f.write ('#msgid "%s"\n'% row[1])
                    #f.write ('#msgstr "%s"\n\n'% row[col])
               # f.close()    
            #col += 1
            #r +=1;

    def generate_pot_file(self):
        xml_doc = xml.dom.minidom.parse(self.source)
        destfile = self.destination

        if os.path.exists(destfile):
            os.remove(destfile)
        f = open(destfile, 'w')

        for node in xml_doc.getElementsByTagName("string"):
            name = node.getAttribute("name")
            for string_element in node.childNodes:
                if string_element.nodeType == Node.TEXT_NODE:
                    f.write('#Key: %s\n'% name)
                    f.write('msgid "%s"\n'% textwrap.fill(string_element.data.replace('\t','').encode('UTF-8')))
                    f.write('msgstr ""\n\n')
        
        """ Include plurals in the string """
        p_elements = xml_doc.getElementsByTagName("plurals")
        if len(p_elements) > 0:
            f.write("##PLURALS##\n")
            for p_element in p_elements:
                item_elements = p_element.getElementsByTagName("item")
                if len(item_elements) > 0:
                    for item_element in item_elements:
                        quantity = item_element.getAttribute("quantity")
                        for item in item_element.childNodes:
                            if item.nodeType == Node.TEXT_NODE:
                                f.write('#Key: %s\n'%quantity)
                                f.write('msgid "%s"\n'% textwrap.fill(item.data.replace('\t','\n').replace('\t',''.strip())))
                                f.write('msgstr ""\n\n')


        f.close()

    def generate_string_from_po_file(self,transFile,lang):
        """ generate string.xml for Android out of the translated po file """
        csv_data = csv.reader(open(self.source,"rb"))  
        doc = xml.dom.minidom.Document()
        resources = doc.createElementNS(
            "urn:oasis:names:tc:xliff:document:1.2","resources")
        doc.appendChild(resources)

        list = []
        tl = self.ReadLines(transFile)
        # sl = self.ReadLines(self.source)
        # print len(tl)
        #print len(sl)
        count = 0
        for i in range(len(tl)):
            if len(tl) > count:
                t = tl[i]
                # l = sl[i]
                msgid = ""
                msgstr = ""
                variable = ""
                if t.startswith("#Key:"):
                    string = doc.createElement("string")
                    s = t.split("#Key:")
                    variable = s[1].strip()
                    string.setAttribute("name",variable.strip("\n"))        
                    sm = tl[i+2].split("msgstr")
                    msgstr = sm[1].strip().strip('"').strip()
                    string_text = doc.createTextNode(msgstr.strip("\n").rstrip('"'))
                    string.appendChild(string_text)
                    resources.appendChild(string)
            else :
                l = sl[i]
                variable = ""
                if l.startswith("#Key:"):
                    s = l.split(" ")
                    variable = s[1]
                if l.startswith("msgid"):
                    s = l.split(" ")
                    msgstr = s[1]
                if not variable:
                    list.append(variable)
                    list.append(msgstr)
            count +=1
        directory = self.create_dir(lang,self.destination)
        stringxml = open(directory+"/strings.xml","w")
        stringxml.writelines(doc.toprettyxml(indent="  "))
        stringxml.close()
        return count


def main(args,options,parser):
    if len(args) < 2:
        parser.print_usage() 
        print "%s: " %(str(len(args)))

    elif len(args) == 2:
        stringgen = StringGenerator(args[0],args[1])
        if options.pot == True:
            stringgen.generate_pot_file()

        if options.csv == True:
            stringgen.generate_pot_from_csv2()

    elif len(args) == 3:
        stringgen = StringGenerator(args[1],args[2])
        stringgen.generate_string_xml(args[0])
    elif len(args) == 4:
        stringgen = StringGenerator(args[1],args[2])
        if options.genstr == True:
            stringgen.generate_string_from_po_file(args[3],args[0])

usage = "usage: %prog <language_2_letter_iso> <source_file> <destination_folder> "+" or %prog [options] <source_file> <destination_file> or "+"%prog <language_2_letter_iso> <source_file> <destination_folder> <translated_po_file>"

parser = OptionParser(usage=usage)

parser.add_option("-p","--pot",action="store_true",help="generate pot file from string.xml file", dest="pot")
parser.add_option("-c","--csv",action="store_true",help="generate pot file from csv file",dest="csv")
parser.add_option("-g","--genstr",action="store_true",help="generate android string file from po file",dest="genstr")
(options, args) = parser.parse_args()

if __name__ == "__main__":
    main(args, options,parser)


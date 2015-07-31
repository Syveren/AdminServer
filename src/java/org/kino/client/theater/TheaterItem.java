/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kino.client.theater;

import com.google.gwt.user.client.rpc.IsSerializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 *
 * @author kirio
 */


public class TheaterItem implements IsSerializable{
        public Integer id;
        public boolean status;
        public Main main;
        public Address address;
        public UridAdress uridAdress;
        public UridInfo uridInfo;
        public ArrayList<ContactData> contactList = new ArrayList<ContactData>();
        public SystemInfo sysInfo;
        public TheaterItem() {
        }

        public TheaterItem(Integer id, boolean status,Main main, Address address, UridAdress uridAdress, UridInfo uridInfo,ArrayList<ContactData> contactList) {
            this.id = id;
               this.status = status;
            this.main = main;
            this.address = address;
            this.uridAdress = uridAdress;
            this.uridInfo = uridInfo;
            this.contactList = contactList;
        }

        

        @Override
        public boolean equals(Object obj) {
            
            return  id.equals(((TheaterItem)obj).id);
        }
        
        
       static public class Main implements IsSerializable{
             public String name;
       
             public String uniqIdent;
             public String n_server;
             public String hdd1;
             public String hdd2;
             public String biospass;
             public String contractNumber;
             public Date contractDate;

            public Main() {
            }

            public Main(String name,  String uniqIdent,String n_server, String hdd1, String hdd2, String biospass, String contractNumber, Date contractDate) {
                this.name = name;
             
                this.uniqIdent = uniqIdent;
                this.n_server = n_server;
                this.hdd1 = hdd1;
                this.hdd2 = hdd2;
                this.biospass = biospass;
                this.contractNumber = contractNumber;
                this.contractDate = contractDate;
            }

            
             
            
        
        }
        
        
        static public class Address implements IsSerializable{
         public String county;
         public String city;
         public String index;
         public String street;
         public String house;

        public Address() {
        }

        public Address(String county, String city, String index, String street, String house) {
            this.county = county;
            this.city = city;
            this.index = index;
            this.street = street;
            this.house = house;
        }
         
   }
   
    static public class UridAdress implements IsSerializable{
         public String county;
         public String city;
         public String index;
         public String street;
         public String house;
         public String phone;
         public String fax;
         public String mail;

        public UridAdress() {
        }

        public UridAdress(String county, String city, String index, String street, String house, String phone, String fax, String mail) {
            this.county = county;
            this.city = city;
            this.index = index;
            this.street = street;
            this.house = house;
            this.phone = phone;
            this.fax = fax;
            this.mail = mail;
        }
         

   }
      static public class UridInfo implements IsSerializable{
         public String name;
         public String dir_fio;
         public String dir_fio_rd;
         public String inn;
         public String kpp;
         public String ogrn;
         public String rs;
         public String bank;
         public String bik;

        public UridInfo() {
        }

        public UridInfo(String name, String dir_fio, String dir_fio_rd, String inn, String kpp, String ogrn, String rs, String bank, String bik) {
            this.name = name;
            this.dir_fio = dir_fio;
            this.dir_fio_rd = dir_fio_rd;
            this.inn = inn;
            this.kpp = kpp;
            this.ogrn = ogrn;
            this.rs = rs;
            this.bank = bank;
            this.bik = bik;
        }
        
        

        }
      
      static public class ContactData implements IsSerializable{
           public int id;
           public String fio;
           public String post;
           public String phone;
           public String email;

        public ContactData() {
        }
        
        public ContactData(int id,String fio, String post, String phone, String email) {
            this.id = id;
            this.fio = fio;
            this.post = post;
            this.phone = phone;
            this.email = email;
        }
            
 
        }
      
      static public class SystemInfo  implements IsSerializable{
          public String vpn_ip  ;
          public long free_space;
          public long total_space;
          public long need_to_load;
          public String version;

            public SystemInfo() {
            }
          
        public SystemInfo( String vpn_ip, long free_space, long total_space, long need_to_load,String version) {
            this.vpn_ip = vpn_ip;
            this.free_space = free_space;
            this.total_space = total_space;
            this.need_to_load = need_to_load;
            this.version = version;
        }
             
        
          
      
      }
    
    }
   
 
  
   
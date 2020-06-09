import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;


public class MongoDB {

    public static void main(String args[]) {
        //Conexion mongoDB
        MongoClient mongo = new MongoClient("localhost", 27017);
        Scanner s = new Scanner(System.in);

        MongoCredential credential = MongoCredential.createCredential("sampleUser", "agenda", "password".toCharArray());

        MongoDatabase database = mongo.getDatabase("agenda");
        MongoCollection<Document> collection = database.getCollection("agenda");
//Config para repetir pregunta inicial
        boolean repe = true;

        while (repe) {
//1 preg
            System.out.println("Que accion quieres realizar consultar | añadir | modificar | eliminar --> ");
            String accion = s.next();
            //1 opcion
            if (accion.equals("consultar")) {
                System.out.print("Quieres visualizar la lista de contactos o un contacto en concreto? general | concreto -->");
                String listados = s.next();

                if (listados.equals("general")) {
                    FindIterable<Document> iterDoc = collection.find();
                    int i = 1;
                    Iterator it = iterDoc.iterator();
                    System.out.println("+----------------------------+\n|         CONTACTOS          |\n+-----+----------------------+\n|  ID |         NOMBRE          |");
                    while (it.hasNext()) {
                        Document d = (Document) it.next();
                        System.out.printf("+-----+----------------------+\n| %3d | %20s |\n", i, d.get("nombre"));
                        i++;
                    }
                    System.out.println("+-----+----------------------+");
                } else if (listados.equals("concreto")) {
                    BasicDBObject whereQuery = new BasicDBObject();

                    System.out.print("Introduce el numero de telefono: ");
                    int telefono = s.nextInt();

                    whereQuery.put("telefono", telefono);
                    FindIterable<Document> doc = collection.find(whereQuery);

                    if (doc.iterator().hasNext()) {
                        System.out.printf(
                                "+----------------------+--------------------------------+----------------------------------------------------+-------------------------------------+\n|         NOM          |            DIRECCI�            |                 CORREU ELECTR�NIC                  |               TELEFONS              |\n+----------------------+--------------------------------+----------------------------------------------------+-------------------------------------+\n| %20s | %30s | %50s | %35s |\n+----------------------+--------------------------------+----------------------------------------------------+-------------------------------------+",
                                doc.iterator().next().get("nombre"), doc.iterator().next().get("direccion"),
                                doc.iterator().next().get("email"), doc.iterator().next().get("telefono"));
                    } else
                        System.out.println("El contacto es invalido o no existe");
                }
                //2 opcion
            } else if (accion.equals("añadir")) {
                System.out.println("Nombre: ");
                String nombre = s.next();
                System.out.println("Direccion: ");
                String direccion = s.next();

                Document contacto = new Document("nombre", nombre).append("direccion", direccion);

                System.out.println("Cuantos emails quieres añadir? --> ");
                int numero_emails = s.nextInt();

                if (numero_emails > 0) {
                    List<String> email;
                    email = new ArrayList<String>();

                    for (int i = 0; i < numero_emails; i++) {
                        System.out.println("Email: ");
                        email.add(s.next());
                    }
                    contacto.append("email", email);
                } else
                    System.out.println("Email no valido");

                System.out.println("Cuantos telefonos quieres añadir?");
                int numero_telefonos = s.nextInt();

                if (numero_telefonos > 0) {
                    List<Integer> telefonos = new ArrayList<Integer>();

                    for (int i = 0; i < numero_telefonos; i++) {
                        System.out.println("Telefono: ");
                        telefonos.add(s.nextInt());
                    }
                    contacto.append("telefono", telefonos);
                } else
                    System.out.println("Cantidad de telefonos no aceptado.");

                collection.insertOne(contacto);
                System.out.println("Contacto añadido correctamente");

                //3 opcion
            } else if (accion.equals("modificar")) {
                BasicDBObject whereQuery = new BasicDBObject();

                System.out.print("Introduce el telefono del contacto ");
                int telefono = s.nextInt();

                whereQuery.put("telefono", telefono);
                FindIterable<Document> doc = collection.find(whereQuery);

                if (doc.iterator().hasNext()) {
                    System.out.printf(
                            "+----------------------+--------------------------------+----------------------------------------------------+-------------------------------------+\n|         NOM          |            DIRECCI�            |                 CORREU ELECTR�NIC                  |               TELEFONS              |\n+----------------------+--------------------------------+----------------------------------------------------+-------------------------------------+\n| %20s | %30s | %50s | %35s |\n+----------------------+--------------------------------+----------------------------------------------------+-------------------------------------+\n",
                            doc.iterator().next().get("nombre"), doc.iterator().next().get("direccion"),
                            doc.iterator().next().get("email"), doc.iterator().next().get("telefono"));

                    System.out.println("Introduce el nombre --> (Escribir null si no quieres canviar el nombre)");
                    String nombre = s.next();

                    collection.updateOne(Filters.eq("telefono", telefono), Updates.set("nombre", nombre));

                    System.out.println("Direccion: (Escribir null si no quieres canviar la direccion)");
                    String direccion = s.next();

                    collection.updateOne(Filters.eq("telefono", telefono), Updates.set("direccion", direccion));

                    System.out.println("Cuantos correos quieres añadir?");
                    int cant_emails = s.nextInt();

                    if (cant_emails > 0) {
                        List<String> emails = new ArrayList<String>();

                        for (int i = 0; i < cant_emails; i++) {
                            System.out.println("Email --> ");
                            emails.add(s.next());
                        }

                        collection.updateOne(Filters.eq("Telefono", telefono), Updates.set("email", emails));

                    } else if (cant_emails == 0)
                        System.out.println("Los correos no han sido modificados");

                    else
                        System.out.println("Cantidad de emails no aceptados.");

                    System.out.println("Cuantos telefonos quieres añadir? ");
                    int cant_telefonos = s.nextInt();

                    if (cant_telefonos > 0) {
                        List<Integer> telefonos = new ArrayList<Integer>();

                        for (int i = 0; i < cant_telefonos; i++) {
                            System.out.println("Tel�fon: ");
                            telefonos.add(s.nextInt());
                        }

                        collection.updateOne(Filters.eq("telefono", telefono), Updates.set("telefono", telefonos));

                    } else if (cant_telefonos == 0)
                        System.out.println("Los telefonos no han sido modificados");
                    else
                        System.out.println("Cantidad de telefonos aceptados.");
                    System.out.println("Contacto modificado correctamente");

                } else
                    System.out.println("El contacto es invalido o no existe");

                //4 opcion
            } else if (accion.equals("eliminar")) {
                System.out.print("Introduce el numero del contacto -->");
                int telefon = s.nextInt();
                System.out.println("Seguro que quieres eliminar el numero " + telefon + " ?");
                String config = s.next();



                if (config.equals("si")) {
                    collection.deleteOne(Filters.eq("telefono", telefon));
                    System.out.println("Contacto eliminado");
                } else if (config.equals("no"))
                    System.out.println("El contacto no se ha eliminado");
                else
                    System.out.println("Error en la confirmacion, no se ha eliminado el contacto");
            }

            //rep
            System.out.println("Quieres continuar? -->");
            String config = s.next();

            if (config.equals("si"))
                repe = true;

            else
                System.out.println("gracias por usar el programa");
            repe = false;


        }
    }
}
package DateBase;


import Models.*;
import dao.*;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.service.ServiceRegistry;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;


public class DBService {
    private static final String hibernate_show_sql = "false";
    private static final String hibernate_format_sql = "false";
    private static final String hibernate_hbm2ddl_auto = "update";

    private final SessionFactory sessionFactory;

    public DBService() {
        Configuration configuration = getMySqlConfiguration();
        sessionFactory = createSessionFactory(configuration);
    }

    @SuppressWarnings("UnusedDeclaration")
    private Configuration getMySqlConfiguration() {
        Configuration configuration = new Configuration();
        configuration.addAnnotatedClass(User.class);
        configuration.addAnnotatedClass(Document.class);
        configuration.addAnnotatedClass(Order.class);

        configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        configuration.setProperty("hibernate.connection.driver_class", "com.mysql.cj.jdbc.Driver");
        configuration.setProperty("hibernate.connection.url", "jdbc:mysql://localhost:3306/itpdb");
        configuration.setProperty("hibernate.connection.serverTimezone", "UTC");
        configuration.setProperty("hibernate.connection.username", "root");
        configuration.setProperty("hibernate.connection.password", "root");
        configuration.setProperty("hibernate.show_sql", hibernate_show_sql);
        configuration.setProperty("hibernate.hbm2ddl.auto", hibernate_hbm2ddl_auto);
        configuration.setProperty("hibernate.format_sql", hibernate_format_sql);
        return configuration;
    }

    //FUNCTIONS
    public User getUser(int id) throws DBException {
        try {
            Session session = sessionFactory.openSession();
            UsersDAO dao = new UsersDAO(session);
            User dataSet = dao.get(id);
            session.close();
            return dataSet;
        } catch (HibernateException e) {
            throw new DBException(e);
        }
    }

    public Order getOrder(int id) throws DBException {
        try {
            Session session = sessionFactory.openSession();
            OrdersDAO dao = new OrdersDAO(session);
            Order dataSet = dao.get(id);
            session.close();
            return dataSet;
        } catch (HibernateException e) {
            throw new DBException(e);
        }
    }

    public Document getDocument(int id) throws DBException {
        try {
            Session session = sessionFactory.openSession();
            DocumentsDAO dao = new DocumentsDAO(session);
            Document dataSet = dao.get(id);
            session.close();
            return dataSet;
        } catch (HibernateException e) {
            throw new DBException(e);
        }
    }

    public List<User> getAllUsers() throws DBException {
        try {
            Session session = sessionFactory.openSession();
            UsersDAO dao = new UsersDAO(session);
            List<User> dataSet = dao.getAll();
            session.close();
            return dataSet;
        } catch (HibernateException e) {
            throw new DBException(e);
        }
    }

    public List<Order> getAllOrders() throws DBException {
        try {
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            OrdersDAO dao = new OrdersDAO(session);
            List<Order> dataSet = dao.getAll();
            session.getTransaction().commit();
            session.close();
            return dataSet;
        } catch (HibernateException e) {
            throw new DBException(e);
        }
    }

    public List<Document> getAllDocuments() throws DBException {
        try {
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            DocumentsDAO dao = new DocumentsDAO(session);
            List<Document> dataSet = dao.getAll();
            session.getTransaction().commit();
            session.close();
            return dataSet;
        } catch (HibernateException e) {
            throw new DBException(e);
        }
    }

    public List<User> getSpecialUsers() throws DBException {
        try {
            Session session = sessionFactory.openSession();
            UsersDAO dao = new UsersDAO(session);
            List<User> dataSet = dao.getSpecialUsers();
            session.close();
            return dataSet;
        } catch (HibernateException e) {
            throw new DBException(e);
        }
    }

    public List<Order> getSpecialOrders(String sqlCondition) throws DBException {
        try {
            Session session = sessionFactory.openSession();
            OrdersDAO dao = new OrdersDAO(session);
            List<Order> dataSet = dao.getSpecialSet(sqlCondition);
            session.close();
            return dataSet;
        } catch (HibernateException e) {
            throw new DBException(e);
        }
    }


    //IT DOES NOT WORK, BECAUSE IT  RETURNS MORE THEN ONE OBJECT
    /*public User getUser(String name) throws DBException{
        try {
            Session session = sessionFactory.openSession();
            UsersDAO dao = new UsersDAO(session);
            User dataSet= dao.get(dao.getUserId(name));
            session.close();
            return dataSet;
        } catch (HibernateException e){
            throw new DBException(e);
        }
    }*/

    public int insertNewUser(String email, String password, String name, String surname, String cookieId, String status, int fine, String address, String phone) throws DBException {
        try {
            Session session = sessionFactory.openSession();
            Transaction transaction = session.beginTransaction();
            UsersDAO dao = new UsersDAO(session);
            int id = dao.insertNew(email, password, name, surname, cookieId, status, fine, address, phone);
            transaction.commit();
            session.close();
            return id;
        } catch (HibernateException e) {
            throw new DBException(e);
        }
    }

    public void updateUser(User user) throws DBException {
        try {
            Session session = sessionFactory.openSession();
            Transaction transaction = session.beginTransaction();
            UsersDAO dao = new UsersDAO(session);
            dao.update(user);
            transaction.commit();
            session.close();
        } catch (HibernateException e) {
            throw new DBException(e);
        }
    }

    public List<User> getListOfUsers(String fieldName1, String value1, String fieldName2, String value2) throws DBException {
        try {
            Session session = sessionFactory.openSession();
            UsersDAO dao = new UsersDAO(session);
            List<User> dataSet = dao.getListOfUsers(fieldName1, value1, fieldName2, value2);
            session.close();
            return dataSet;
        } catch (HibernateException e) {
            throw new DBException(e);
        }
    }


    public void printConnectInfo() {
        try {
            SessionFactoryImpl sessionFactoryImpl = (SessionFactoryImpl) sessionFactory;
            Connection connection = sessionFactoryImpl.getConnectionProvider().getConnection();
            System.out.println("DB name: " + connection.getMetaData().getDatabaseProductName());
            System.out.println("DB version: " + connection.getMetaData().getDatabaseProductVersion());
            System.out.println("Driver: " + connection.getMetaData().getDriverName());
            System.out.println("Autocommit: " + connection.getAutoCommit());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static SessionFactory createSessionFactory(Configuration configuration) {
        StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder();
        builder.applySettings(configuration.getProperties());
        ServiceRegistry serviceRegistry = builder.build();
        return configuration.buildSessionFactory(serviceRegistry);
    }
}

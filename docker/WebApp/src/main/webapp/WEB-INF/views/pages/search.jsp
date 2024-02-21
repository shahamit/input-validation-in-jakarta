<%@ page contentType="text/html;charset=UTF-8" language="java" %> <%@ taglib
prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
  <head>
    <%@ include file="../partials/head.jsp" %>

    <title>Search for logs</title>
  </head>
  <body class="py-4">
    <form class="d-flex" method="get">
      <input
        class="form-control me-2"
        aria-label="Search"
        name="q"
        placeholder="e.g., ip.src==192.168.0.1 AND ip.dst==10.0.0.1 AND tcp.port==80"
        type="search"
        value="${q}"
      />
      <button class="btn btn-outline-success" type="submit">Search</button>
    </form>
    <table class="table table-striped table-hover m-0">
      <tr>
        <th>Id</th>
        <th>Source IP</th>
        <th>Destination IP</th>
        <th>Port</th>
        <th>Date</th>
      </tr>
      <c:forEach var="log" items="${logs}">
        <tr>
          <td>${log.id}</td>
          <td>${log.sourceIP}</td>
          <td>${log.destinationIP}</td>
          <td>${log.port}</td>
          <td>${log.date}</td>
        </tr>
      </c:forEach>
    </table>

    <footer><%@ include file="../partials/footer.jsp" %></footer>
  </body>
</html>

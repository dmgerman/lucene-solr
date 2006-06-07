begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.gdata.servlet
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|servlet
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletRequest
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|easymock
operator|.
name|MockControl
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_comment
comment|/**   * @author Simon Willnauer   *   */
end_comment

begin_class
DECL|class|TestAbstractGdataServlet
specifier|public
class|class
name|TestAbstractGdataServlet
extends|extends
name|TestCase
block|{
DECL|field|METHOD_DELETE
specifier|private
specifier|static
specifier|final
name|String
name|METHOD_DELETE
init|=
literal|"DELETE"
decl_stmt|;
DECL|field|METHOD_GET
specifier|private
specifier|static
specifier|final
name|String
name|METHOD_GET
init|=
literal|"GET"
decl_stmt|;
DECL|field|METHOD_POST
specifier|private
specifier|static
specifier|final
name|String
name|METHOD_POST
init|=
literal|"POST"
decl_stmt|;
DECL|field|METHOD_PUT
specifier|private
specifier|static
specifier|final
name|String
name|METHOD_PUT
init|=
literal|"PUT"
decl_stmt|;
DECL|field|METHOD_HEADER_NAME
specifier|private
specifier|static
specifier|final
name|String
name|METHOD_HEADER_NAME
init|=
literal|"x-http-method-override"
decl_stmt|;
DECL|field|mockRequest
specifier|private
name|HttpServletRequest
name|mockRequest
init|=
literal|null
decl_stmt|;
DECL|field|mockResponse
specifier|private
name|HttpServletResponse
name|mockResponse
init|=
literal|null
decl_stmt|;
DECL|field|servletInstance
specifier|private
name|AbstractGdataServlet
name|servletInstance
init|=
literal|null
decl_stmt|;
DECL|field|requestMockControl
specifier|private
name|MockControl
name|requestMockControl
decl_stmt|;
DECL|field|responseMockControl
specifier|private
name|MockControl
name|responseMockControl
decl_stmt|;
DECL|method|setUp
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|this
operator|.
name|requestMockControl
operator|=
name|MockControl
operator|.
name|createControl
argument_list|(
name|HttpServletRequest
operator|.
name|class
argument_list|)
expr_stmt|;
name|this
operator|.
name|responseMockControl
operator|=
name|MockControl
operator|.
name|createControl
argument_list|(
name|HttpServletResponse
operator|.
name|class
argument_list|)
expr_stmt|;
name|this
operator|.
name|mockRequest
operator|=
operator|(
name|HttpServletRequest
operator|)
name|this
operator|.
name|requestMockControl
operator|.
name|getMock
argument_list|()
expr_stmt|;
name|this
operator|.
name|mockResponse
operator|=
operator|(
name|HttpServletResponse
operator|)
name|this
operator|.
name|responseMockControl
operator|.
name|getMock
argument_list|()
expr_stmt|;
name|this
operator|.
name|servletInstance
operator|=
operator|new
name|StubGDataServlet
argument_list|()
expr_stmt|;
block|}
comment|/**       * Test method for       * 'org.apache.lucene.gdata.servlet.AbstractGdataServlet.service(HttpServletRequest,       * HttpServletResponse)'       */
DECL|method|testServiceHttpServletRequestHttpServletResponseDelete
specifier|public
name|void
name|testServiceHttpServletRequestHttpServletResponseDelete
parameter_list|()
block|{
name|this
operator|.
name|requestMockControl
operator|.
name|expectAndDefaultReturn
argument_list|(
name|this
operator|.
name|mockRequest
operator|.
name|getMethod
argument_list|()
argument_list|,
name|METHOD_DELETE
argument_list|)
expr_stmt|;
name|this
operator|.
name|requestMockControl
operator|.
name|expectAndDefaultReturn
argument_list|(
name|this
operator|.
name|mockRequest
operator|.
name|getHeader
argument_list|(
name|METHOD_HEADER_NAME
argument_list|)
argument_list|,
name|METHOD_DELETE
argument_list|)
expr_stmt|;
name|this
operator|.
name|requestMockControl
operator|.
name|replay
argument_list|()
expr_stmt|;
try|try
block|{
name|this
operator|.
name|servletInstance
operator|.
name|service
argument_list|(
name|this
operator|.
name|mockRequest
argument_list|,
name|this
operator|.
name|mockResponse
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ServletException
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"ServeltExpception not expected"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"IOExpception not expected"
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|requestMockControl
operator|.
name|verify
argument_list|()
expr_stmt|;
name|this
operator|.
name|requestMockControl
operator|.
name|reset
argument_list|()
expr_stmt|;
name|this
operator|.
name|requestMockControl
operator|.
name|expectAndDefaultReturn
argument_list|(
name|this
operator|.
name|mockRequest
operator|.
name|getMethod
argument_list|()
argument_list|,
name|METHOD_POST
argument_list|)
expr_stmt|;
name|this
operator|.
name|requestMockControl
operator|.
name|expectAndDefaultReturn
argument_list|(
name|this
operator|.
name|mockRequest
operator|.
name|getHeader
argument_list|(
name|METHOD_HEADER_NAME
argument_list|)
argument_list|,
name|METHOD_DELETE
argument_list|)
expr_stmt|;
name|this
operator|.
name|requestMockControl
operator|.
name|replay
argument_list|()
expr_stmt|;
try|try
block|{
name|this
operator|.
name|servletInstance
operator|.
name|service
argument_list|(
name|this
operator|.
name|mockRequest
argument_list|,
name|this
operator|.
name|mockResponse
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ServletException
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"ServeltExpception not expected"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"IOExpception not expected"
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|requestMockControl
operator|.
name|verify
argument_list|()
expr_stmt|;
block|}
comment|/**       *        */
DECL|method|testServiceNullOverrideHeader
specifier|public
name|void
name|testServiceNullOverrideHeader
parameter_list|()
block|{
name|this
operator|.
name|requestMockControl
operator|.
name|expectAndDefaultReturn
argument_list|(
name|this
operator|.
name|mockRequest
operator|.
name|getMethod
argument_list|()
argument_list|,
name|METHOD_POST
argument_list|)
expr_stmt|;
name|this
operator|.
name|requestMockControl
operator|.
name|expectAndDefaultReturn
argument_list|(
name|this
operator|.
name|mockRequest
operator|.
name|getHeader
argument_list|(
name|METHOD_HEADER_NAME
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|requestMockControl
operator|.
name|replay
argument_list|()
expr_stmt|;
try|try
block|{
name|this
operator|.
name|servletInstance
operator|.
name|service
argument_list|(
name|this
operator|.
name|mockRequest
argument_list|,
name|this
operator|.
name|mockResponse
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ServletException
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"ServeltExpception not expected"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"IOExpception not expected"
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|requestMockControl
operator|.
name|verify
argument_list|()
expr_stmt|;
name|this
operator|.
name|requestMockControl
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
comment|/**       * Test method for       * 'org.apache.lucene.gdata.servlet.AbstractGdataServlet.service(HttpServletRequest,       * HttpServletResponse)'       */
DECL|method|testServiceHttpServletRequestHttpServletResponsePOST
specifier|public
name|void
name|testServiceHttpServletRequestHttpServletResponsePOST
parameter_list|()
block|{
name|this
operator|.
name|requestMockControl
operator|.
name|expectAndDefaultReturn
argument_list|(
name|this
operator|.
name|mockRequest
operator|.
name|getMethod
argument_list|()
argument_list|,
name|METHOD_POST
argument_list|)
expr_stmt|;
name|this
operator|.
name|requestMockControl
operator|.
name|expectAndDefaultReturn
argument_list|(
name|this
operator|.
name|mockRequest
operator|.
name|getHeader
argument_list|(
name|METHOD_HEADER_NAME
argument_list|)
argument_list|,
name|METHOD_POST
argument_list|)
expr_stmt|;
name|this
operator|.
name|requestMockControl
operator|.
name|replay
argument_list|()
expr_stmt|;
try|try
block|{
name|this
operator|.
name|servletInstance
operator|.
name|service
argument_list|(
name|this
operator|.
name|mockRequest
argument_list|,
name|this
operator|.
name|mockResponse
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ServletException
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"ServeltExpception not expected"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"IOExpception not expected"
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|requestMockControl
operator|.
name|verify
argument_list|()
expr_stmt|;
name|this
operator|.
name|requestMockControl
operator|.
name|reset
argument_list|()
expr_stmt|;
name|this
operator|.
name|requestMockControl
operator|.
name|expectAndDefaultReturn
argument_list|(
name|this
operator|.
name|mockRequest
operator|.
name|getMethod
argument_list|()
argument_list|,
name|METHOD_PUT
argument_list|)
expr_stmt|;
name|this
operator|.
name|requestMockControl
operator|.
name|expectAndDefaultReturn
argument_list|(
name|this
operator|.
name|mockRequest
operator|.
name|getHeader
argument_list|(
name|METHOD_HEADER_NAME
argument_list|)
argument_list|,
name|METHOD_POST
argument_list|)
expr_stmt|;
name|this
operator|.
name|requestMockControl
operator|.
name|replay
argument_list|()
expr_stmt|;
try|try
block|{
name|this
operator|.
name|servletInstance
operator|.
name|service
argument_list|(
name|this
operator|.
name|mockRequest
argument_list|,
name|this
operator|.
name|mockResponse
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ServletException
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"ServeltExpception not expected"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"IOExpception not expected"
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|requestMockControl
operator|.
name|verify
argument_list|()
expr_stmt|;
block|}
comment|/**       * Test method for       * 'org.apache.lucene.gdata.servlet.AbstractGdataServlet.service(HttpServletRequest,       * HttpServletResponse)'       */
DECL|method|testServiceHttpServletRequestHttpServletResponsePUT
specifier|public
name|void
name|testServiceHttpServletRequestHttpServletResponsePUT
parameter_list|()
block|{
name|this
operator|.
name|requestMockControl
operator|.
name|expectAndDefaultReturn
argument_list|(
name|this
operator|.
name|mockRequest
operator|.
name|getMethod
argument_list|()
argument_list|,
name|METHOD_PUT
argument_list|)
expr_stmt|;
name|this
operator|.
name|requestMockControl
operator|.
name|expectAndDefaultReturn
argument_list|(
name|this
operator|.
name|mockRequest
operator|.
name|getHeader
argument_list|(
name|METHOD_HEADER_NAME
argument_list|)
argument_list|,
name|METHOD_PUT
argument_list|)
expr_stmt|;
name|this
operator|.
name|requestMockControl
operator|.
name|replay
argument_list|()
expr_stmt|;
try|try
block|{
name|this
operator|.
name|servletInstance
operator|.
name|service
argument_list|(
name|this
operator|.
name|mockRequest
argument_list|,
name|this
operator|.
name|mockResponse
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ServletException
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"ServeltExpception not expected"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"IOExpception not expected"
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|requestMockControl
operator|.
name|verify
argument_list|()
expr_stmt|;
name|this
operator|.
name|requestMockControl
operator|.
name|reset
argument_list|()
expr_stmt|;
name|this
operator|.
name|requestMockControl
operator|.
name|expectAndDefaultReturn
argument_list|(
name|this
operator|.
name|mockRequest
operator|.
name|getMethod
argument_list|()
argument_list|,
name|METHOD_POST
argument_list|)
expr_stmt|;
name|this
operator|.
name|requestMockControl
operator|.
name|expectAndDefaultReturn
argument_list|(
name|this
operator|.
name|mockRequest
operator|.
name|getHeader
argument_list|(
name|METHOD_HEADER_NAME
argument_list|)
argument_list|,
name|METHOD_PUT
argument_list|)
expr_stmt|;
name|this
operator|.
name|requestMockControl
operator|.
name|replay
argument_list|()
expr_stmt|;
try|try
block|{
name|this
operator|.
name|servletInstance
operator|.
name|service
argument_list|(
name|this
operator|.
name|mockRequest
argument_list|,
name|this
operator|.
name|mockResponse
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ServletException
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"ServeltExpception not expected"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"IOExpception not expected"
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|requestMockControl
operator|.
name|verify
argument_list|()
expr_stmt|;
block|}
comment|/**       * Test method for       * 'org.apache.lucene.gdata.servlet.AbstractGdataServlet.service(HttpServletRequest,       * HttpServletResponse)'       */
DECL|method|testServiceHttpServletRequestHttpServletResponseGET
specifier|public
name|void
name|testServiceHttpServletRequestHttpServletResponseGET
parameter_list|()
block|{
name|this
operator|.
name|requestMockControl
operator|.
name|expectAndDefaultReturn
argument_list|(
name|this
operator|.
name|mockRequest
operator|.
name|getMethod
argument_list|()
argument_list|,
name|METHOD_GET
argument_list|)
expr_stmt|;
name|this
operator|.
name|requestMockControl
operator|.
name|expectAndDefaultReturn
argument_list|(
name|this
operator|.
name|mockRequest
operator|.
name|getHeader
argument_list|(
name|METHOD_HEADER_NAME
argument_list|)
argument_list|,
name|METHOD_GET
argument_list|)
expr_stmt|;
name|this
operator|.
name|requestMockControl
operator|.
name|replay
argument_list|()
expr_stmt|;
try|try
block|{
name|this
operator|.
name|servletInstance
operator|.
name|service
argument_list|(
name|this
operator|.
name|mockRequest
argument_list|,
name|this
operator|.
name|mockResponse
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ServletException
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"ServeltExpception not expected"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"IOExpception not expected"
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|requestMockControl
operator|.
name|verify
argument_list|()
expr_stmt|;
name|this
operator|.
name|requestMockControl
operator|.
name|reset
argument_list|()
expr_stmt|;
name|this
operator|.
name|requestMockControl
operator|.
name|expectAndDefaultReturn
argument_list|(
name|this
operator|.
name|mockRequest
operator|.
name|getMethod
argument_list|()
argument_list|,
name|METHOD_POST
argument_list|)
expr_stmt|;
name|this
operator|.
name|requestMockControl
operator|.
name|expectAndDefaultReturn
argument_list|(
name|this
operator|.
name|mockRequest
operator|.
name|getHeader
argument_list|(
name|METHOD_HEADER_NAME
argument_list|)
argument_list|,
name|METHOD_GET
argument_list|)
expr_stmt|;
name|this
operator|.
name|requestMockControl
operator|.
name|replay
argument_list|()
expr_stmt|;
try|try
block|{
name|this
operator|.
name|servletInstance
operator|.
name|service
argument_list|(
name|this
operator|.
name|mockRequest
argument_list|,
name|this
operator|.
name|mockResponse
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ServletException
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"ServeltExpception not expected"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"IOExpception not expected"
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|requestMockControl
operator|.
name|verify
argument_list|()
expr_stmt|;
block|}
comment|/**       * Stub Implementation for<code>AbstractGdataServlet</code>       * @author Simon Willnauer       *       */
DECL|class|StubGDataServlet
specifier|static
class|class
name|StubGDataServlet
extends|extends
name|AbstractGdataServlet
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|6271464588547620925L
decl_stmt|;
DECL|method|doDelete
specifier|protected
name|void
name|doDelete
parameter_list|(
name|HttpServletRequest
name|arg0
parameter_list|,
name|HttpServletResponse
name|arg1
parameter_list|)
block|{
if|if
condition|(
name|arg0
operator|.
name|getHeader
argument_list|(
name|METHOD_HEADER_NAME
argument_list|)
operator|==
literal|null
condition|)
name|assertEquals
argument_list|(
literal|"Http-Method --DELETE--"
argument_list|,
name|METHOD_DELETE
argument_list|,
name|arg0
operator|.
name|getMethod
argument_list|()
argument_list|)
expr_stmt|;
else|else
name|assertEquals
argument_list|(
literal|"Http-Method override --DELETE--"
argument_list|,
name|METHOD_DELETE
argument_list|,
name|arg0
operator|.
name|getHeader
argument_list|(
name|METHOD_HEADER_NAME
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|doGet
specifier|protected
name|void
name|doGet
parameter_list|(
name|HttpServletRequest
name|arg0
parameter_list|,
name|HttpServletResponse
name|arg1
parameter_list|)
block|{
if|if
condition|(
name|arg0
operator|.
name|getHeader
argument_list|(
name|METHOD_HEADER_NAME
argument_list|)
operator|==
literal|null
condition|)
name|assertEquals
argument_list|(
literal|"Http-Method --GET--"
argument_list|,
name|arg0
operator|.
name|getMethod
argument_list|()
argument_list|,
name|METHOD_GET
argument_list|)
expr_stmt|;
else|else
name|assertEquals
argument_list|(
literal|"Http-Method override --GET--"
argument_list|,
name|arg0
operator|.
name|getHeader
argument_list|(
name|METHOD_HEADER_NAME
argument_list|)
argument_list|,
name|METHOD_GET
argument_list|)
expr_stmt|;
block|}
DECL|method|doPost
specifier|protected
name|void
name|doPost
parameter_list|(
name|HttpServletRequest
name|arg0
parameter_list|,
name|HttpServletResponse
name|arg1
parameter_list|)
block|{
if|if
condition|(
name|arg0
operator|.
name|getHeader
argument_list|(
name|METHOD_HEADER_NAME
argument_list|)
operator|==
literal|null
condition|)
name|assertEquals
argument_list|(
literal|"Http-Method --POST--"
argument_list|,
name|arg0
operator|.
name|getMethod
argument_list|()
argument_list|,
name|METHOD_POST
argument_list|)
expr_stmt|;
else|else
name|assertEquals
argument_list|(
literal|"Http-Method override --POST--"
argument_list|,
name|METHOD_POST
argument_list|,
name|arg0
operator|.
name|getHeader
argument_list|(
name|METHOD_HEADER_NAME
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|doPut
specifier|protected
name|void
name|doPut
parameter_list|(
name|HttpServletRequest
name|arg0
parameter_list|,
name|HttpServletResponse
name|arg1
parameter_list|)
block|{
if|if
condition|(
name|arg0
operator|.
name|getHeader
argument_list|(
name|METHOD_HEADER_NAME
argument_list|)
operator|==
literal|null
condition|)
name|assertEquals
argument_list|(
literal|"Http-Method --PUT--"
argument_list|,
name|arg0
operator|.
name|getMethod
argument_list|()
argument_list|,
name|METHOD_PUT
argument_list|)
expr_stmt|;
else|else
name|assertEquals
argument_list|(
literal|"Http-Method override --PUT--"
argument_list|,
name|arg0
operator|.
name|getHeader
argument_list|(
name|METHOD_HEADER_NAME
argument_list|)
argument_list|,
name|METHOD_PUT
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit


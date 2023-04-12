package me.juge.nsf2wp;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.Writer;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import lotus.domino.Database;
import lotus.domino.DxlExporter;
import lotus.domino.NotesFactory;
import lotus.domino.NotesThread;
import lotus.domino.Session;
import lotus.domino.View;
import lotus.domino.ViewColumn;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class NDJavaLib {
	//. true の場合、XSLT 変換まで（アップロードしない）
	public static boolean offline = false;
	
	//. 以下３行は本番時は不要
	public static String server = null;
	public static String dbpath = "4test.nsf"; //"dev/note01.nsf";
//	public static String repid = "4925784D00085163"; //"492578300081D1EE";

	public static String file_separator = System.getProperty( "file.separator" );
	
	public static Database db = null;
	public static String domain = "";

	
	public static boolean dbg = true;
	public static boolean hidden_view = false;
	public static String xml_encode = "";
	public static String view_list_bgcolor = "#CCCCCC";
	public static String view_bgcolor = "#CCCCCC";
	public static String upload_server = "https://notes2web.appspot.com/";
	public static String post_service = "http://192.168.0.103/nsf2wp_service/nsf2wp_service.php";
	public static String target = null;
	public static int upload_wait = 1000;
	public static String mobile_css = "iphone";
	public static String not_supported_msg = "(Not supported object)";
	public static String standard_xsl = "standard.xsl";

	
	public static String db_folder = "database";
	public static String info_folder = "infos";
	public static String databasescript_folder = "databasescript";
	public static String acl_folder = "acls";
	public static String note_folder = "notes";
	public static String form_folder = "forms";
	public static String subform_folder = "subforms";
	public static String sharedfield_folder = "sharedfields";
	public static String sharedcolumn_folder = "sharedcolumns";
	public static String sharedaction_folder = "sharedactions";
	public static String view_folder = "views";
	public static String folder_folder = "folders";
	public static String launch_folder = "launch";
	public static String frameset_folder = "framesets";
	public static String page_folder = "pages";
	public static String outline_folder = "outlines";
	public static String agent_folder = "agents";
	public static String scriptlibrary_folder = "scriptlibraries";
	public static String helpaboutdocument_folder = "helpabout";
	public static String helpusingdocument_folder = "helpusing";
	public static String imageresource_folder = "imgresources";
	public static String fileresource_folder = "fileresources";
	public static String stylesheetresource_folder = "cssresources";
	public static String appletresource_folder = "appletresources";

	public static String doc_folder = "docs";
	
	
	//. 新規追加
	public static String form_xml_folder = "form_xml";
	public static String view_xml_folder = "view_xml";
	public static String folder_xml_folder = "folder_xml";
	public static String doc_xml_folder = "doc_xml";

	
	public static String picture_folder = "imgs";
	public static String file_folder = "files";
	public static String sys_imgs_folder = "sys_imgs";
	public static String sys_imgs_db = "db.gif";
	public static String sys_imgs_view = "view.gif";
	public static String sys_imgs_doc = "doc.gif";
	public static String sys_imgs_fold = "folder.gif";
	public static String sys_imgs_dbicon = "dbicon.gif";
	
	public static int[] dbicon_bg = new int[32*32];
	public static int[] dbicon_fg = new int[32*32];
	public static Color[] dbicon_color = {
		new Color( 0, 0, 0 ),
		new Color( 255, 255, 255 ),
		new Color( 255, 0, 0 ),
		new Color( 0, 255, 0 ),
		new Color( 0, 0, 255 ),
		new Color( 255, 0, 255 ),
		new Color( 255, 255, 0 ),
		new Color( 0, 255, 255 ),
		new Color( 128, 0, 0 ),
		new Color( 0, 128, 0 ),
		new Color( 0, 0, 128 ),
		new Color( 128, 0, 128 ),
		new Color( 128, 128, 0 ),
		new Color( 0, 128, 128 ),
		new Color( 70, 70, 70 ),
		new Color( 128, 128, 128 ),
		new Color( 200, 200, 200 )  //. 背景
	};

	//. 変換結果の格納フォルダ（絶対に消しちゃダメ）
	public static String html_folder = "htmls";

	//. 以下は文書とビューの関係を格納するフォルダ
	public static String doc_in_view_folder = "docview";	
	
	
	//. UNID と WordPress ID との紐付け用
	public static HashMap<String,String> unid2wpid = null;

	//. 投稿者のデフォルトID
	public static String author_id = "1";	
	
	//. タイトルを取得するためのデフォルト式
	public static String def_doc_title_formula = "", doc_title_formula = "";
	
	
	//. 国際化対応
//	public static Locale loc = Locale.getDefault();
//	public static ResourceBundle rb = ResourceBundle.getBundle( "MessageResource", loc );

	//. 外部向け公開メソッド
	
	
	public static String GetRepid( String server, String filepath ){
		String rid = null;
		
		try{
			NotesThread.sinitThread();
			
			Session ns = NotesFactory.createSessionWithFullAccess();
			Database ndb = ns.getDatabase( server, filepath );
			if( !ndb.isOpen() ){
				ndb.open();
			}		
			
			rid = ndb.getReplicaID();
		}catch( Exception e ){
			e.printStackTrace();
		}finally{
			NotesThread.stermThread();
		}
		
		return rid;
	}
	
	
	@SuppressWarnings("unchecked")
	public static String ExportDb( String server, String filepath, String ddserver, String ddfilepath ){
		String repid = null;
		
		try{
			//. 初期設定読み込み
//			readParam( "n2h.ini" );
			
			
			//. Step 1-1 : DXL エクスポートと、各要素への分解
			NotesThread.sinitThread();
			
			Session s = NotesFactory.createSessionWithFullAccess();
			db = s.getDatabase( server, filepath );
			if( !db.isOpen() ){
				db.open();
			}		
			
			DxlExporter exporter = s.createDxlExporter();
			exporter.setOutputDOCTYPE( false );
			exporter.setConvertNotesBitmapsToGIF( true );
			String dxl = exporter.exportDxl( db );
//			DbgPrintln( dxl );
				
			Element databaseElement = getRootElement( dxl ); // <database>
			repid = databaseElement.getAttribute( "replicaid" );
//			DbgPrintln( "ReplicaID = " + repid );
				
			if( removeFolder( repid ) > 0 ){
				//. フォルダや文書を削除した場合は待つ
				DbgPrint( "Preparing..." );
				try{
					Thread.sleep( upload_wait );
				}catch( Exception e ){
				}
				DbgPrintln( "Done." );
			}
			createFolder( repid );

			//. DB 本体の DXL そのものを保存 (-> /(RepId)/ )
			Writer out1 = new OutputStreamWriter( new FileOutputStream( repid + "/" + repid + ".xml" ), "UTF-8" );
			out1.write( dxl );
			out1.close();

			DbgPrintln( "DXL export finished." );
			
			//. UTF-8 で読み込みなおし
			//. ここはもっと高速化できる(K.Kimura)
			databaseElement = getRootElement( new File( repid + "/" + repid + ".xml" ) ); // <database>						
			createFolder( repid + "/" + db_folder );

			//. <database> の全属性を取り出す
			String db_xml = "<database";
			NamedNodeMap attrs = databaseElement.getAttributes();
			if( attrs != null ){
				for( int i = 0; i < attrs.getLength(); i ++ ){
					Node attr = attrs.item( i );
					String attrName = attr.getNodeName();
					String attrValue = sanitize( attr.getNodeValue() );
					db_xml += ( " " + attrName + "='" + attrValue + "'" );
				}
			}
			db_xml += ( "/>" );
			
			//. <database> 部分だけを取り出して保存 (-> /(RepId)/database/ )
			String dbfilename = repid + "/" + db_folder + "/" + repid + ".xml";
			writeFileUTF8( dbfilename, db_xml );
			
			//. 変換結果格納フォルダ
			createFolder( repid + "/" + html_folder );

			//. アイコンを探す
			createFolder( repid + "/" + note_folder );
			createFolder( repid + "/" + imageresource_folder );
			createFolder( repid + "/" + html_folder + "/" + picture_folder );

			//. Lotus Notes 8.5.2 以降のエクスポートアイコンを探す
			boolean found = false;
			NodeList imgresList = databaseElement.getElementsByTagName( "imageresource" );
			int nImgresList = imgresList.getLength();
			for( int i = 0; i < nImgresList; i ++ ){
				Element imgresElement = ( Element )imgresList.item( i );
				String imgresName = imgresElement.getAttribute( "name" );
				if( imgresName.equals( "$DBIcon" ) ){
					String imagename = imgresElement.getAttribute( "imagename" );
					int n = imagename.lastIndexOf( "." );
					String imageext = imagename.substring( n );  //. .gif, .jpg, .png, ..
					
					NodeList imgList = imgresElement.getElementsByTagName( "gif" );
					if( imgList.getLength() == 0 ){
						imgList = imgresElement.getElementsByTagName( "jpeg" );
					}
					
					byte[] data = null;
					if( imgList.getLength() > 0 ){
						Element imgElement = ( Element )imgList.item( 0 );
						String bitmapdata = imgElement.getFirstChild().getNodeValue();
						if( bitmapdata.length() > 0 ){
							data = B64decode( bitmapdata );
						}
					}
					
					if( data != null ){
						//. 一旦保存
						try{
							File f = new File( repid + "/" + imageresource_folder + "/" + sys_imgs_dbicon + imageext );
							BufferedOutputStream bos = new BufferedOutputStream( new FileOutputStream( f ) );
							bos.write( data, 0, data.length );
							bos.close();
							
							//. 32x32 を 64x64 にリサイズして読み込み
							BufferedImage srcimg = null, dstimg = null;
							try{
								srcimg = ImageIO.read( f );
							}catch( Exception e ){
								e.printStackTrace();
								srcimg = null;
							}
							
							try{
								dstimg = new BufferedImage( 64, 64, BufferedImage.TYPE_INT_ARGB );  //. NGリスト: TYPE_BYTE_BINARY
							}catch( Exception e ){
								e.printStackTrace();
								dstimg = null;
							}

							if( srcimg != null && dstimg != null ){
								try{
									int w = srcimg.getWidth();
									double sx = ( double )64.0 / w;
									int h = srcimg.getHeight();
									double sy = ( double )64.0 / h;
									AffineTransform trans = AffineTransform.getScaleInstance( sx, sy );
									
									Graphics2D g2 = ( Graphics2D )dstimg.createGraphics();
									g2.drawImage( srcimg, trans, null );
									g2.dispose();
									
									File outf = new File( repid + "/" + html_folder + "/" + picture_folder + "/" + sys_imgs_dbicon );
									ImageIO.write( dstimg, "gif", outf );
								}catch( Exception e ){
									e.printStackTrace();
								}
							}
						}catch( Exception e ){
							e.printStackTrace();
						}
					}
					
					found = true;
					break;
				}
			}
			
			//. note を処理
			NodeList noteList = databaseElement.getElementsByTagName( "note" );
			int nNoteList = noteList.getLength();
			for( int i = 0; i < nNoteList; i ++ ){
				Element noteElement = ( Element )noteList.item( i );
				String unid = getUnid( noteElement );
				String filename = repid + "/" + note_folder + "/" + unid + ".xml";
				WriteElementToXmlFile( filename, noteElement );
				
				if( !found ){
					//. アイコンを探す
					byte[] data = null;
					
					//. クラシックアイコンの調査
					NodeList itemList = noteElement.getElementsByTagName( "item" );
					int nItemList = itemList.getLength();
					for( int j = 0; j < nItemList; j ++ ){
						Element itemElement = ( Element )itemList.item( j );
						String name = itemElement.getAttribute( "name" );
						if( name.toLowerCase().equals( "iconbitmap" ) ){
							NodeList list = itemElement.getElementsByTagName( "rawitemdata" );
							Element element = ( Element )list.item( 0 );
							String bitmapdata = element.getFirstChild().getNodeValue();
							data = B64decode( bitmapdata );
							break;
						}
					}
						
					if( data != null ){
						//. 背景データ
						for( int j = 0x0006, k = 0; j <= 0x0085; j ++ ){
							int b = ( int )data[j];
							
							//. 背景データは１ビットずつ確認する
							for( int l = 0; l < 8; l ++ ){
								int mask = 128 >> l;
								int x = ( b & mask ) >> ( 7 - l );
								dbicon_bg[k+l] = x;
							}
							k += 8;
						}
							
						//. 前景データ
						for( int j = 0x0086, k = 0; j <= 0x0285; j ++ ){
							int b = ( int )data[j];
				
							//. 前景データは４ビットずつ確認する
							for( int l = 0; l < 2; l ++ ){
								int mask = 0xf0 >> ( l * 4 );
								int x = ( b & mask ) >> ( 4 - ( l * 4 ) );
								dbicon_fg[k+l] = x;
							}
							k += 2;
						}

						//. 可能ならば透過GIFにしたい・・・
//						BufferedImage bimg = new BufferedImage( 64, 64, BufferedImage.TYPE_BYTE_INDEXED );
						BufferedImage bimg = new BufferedImage( 64, 64, BufferedImage.TYPE_INT_BGR );
						Graphics2D g2 = bimg.createGraphics();
						//. g2 に図形を描画
						for( int j = 0; j < 32 * 32; j ++ ){
							int x = ( j % 32 ) * 2;
							int y = ( 31 - ( j / 32 ) ) * 2;
							if( dbicon_bg[j] == 1 ){
								g2.setColor( dbicon_color[16] );
							}else{
								g2.setColor( dbicon_color[dbicon_fg[j]] );
							}
								
							g2.fillRect( x, y, 2, 2 );
						}
							
						g2.drawImage( bimg, 0, 0, null );
							
						try{
							//. モバイル化対応  (-> /(RepId)/htmls/imgs/dbicon.gif )
							File f = new File( repid + "/" + html_folder + "/" + picture_folder + "/" + sys_imgs_dbicon );
							if( ImageIO.write( bimg, "gif", f ) ){
								DbgPrintln( "(DB Icon retrieved.)" );
							}
						}catch( Exception e ){
						}
					}
				}
			}
			DbgPrintln( nNoteList + " <note>s retrieved." );

			//. フォームを処理  (-> /(RepId)/forms )
			NodeList formList = databaseElement.getElementsByTagName( "form" );
			int nFormList = formList.getLength();
			createFolder( repid + "/" + form_folder );
			for( int i = 0; i < nFormList; i ++ ){
				Element formElement = ( Element )formList.item( i );
				String unid = getUnid( formElement );
				String filename = repid + "/" + form_folder + "/" + unid + ".xml";
				WriteElementToXmlFile( filename, formElement );
				
				//. この後、共有要素をマージ
			}
			DbgPrintln( nFormList + " <form>s retrieved." );

			//. サブフォームを処理  (-> /(RepId)/subforms )
			NodeList subformList = databaseElement.getElementsByTagName( "subform" );
			int nSubformList = subformList.getLength();
			createFolder( repid + "/" + subform_folder );
			for( int i = 0; i < nSubformList; i ++ ){
				Element subformElement = ( Element )subformList.item( i );
				String unid = getUnid( subformElement );
				String filename = repid + "/" + subform_folder + "/" + unid + ".xml";
				WriteElementToXmlFile( filename, subformElement );
			}
			DbgPrintln( nSubformList + " <subform>s retrieved." );

			//. 共用フィールドを処理  (-> /(RepId)/sharedfields )
			NodeList sharedfieldList = databaseElement.getElementsByTagName( "sharedfield" );
			int nSharedfieldList = sharedfieldList.getLength();
			createFolder( repid + "/" + sharedfield_folder );
			for( int i = 0; i < nSharedfieldList; i ++ ){
				Element sharedfieldElement = ( Element )sharedfieldList.item( i );
				String unid = getUnid( sharedfieldElement );
				String filename = repid + "/" + sharedfield_folder + "/" + unid + ".xml";
				WriteElementToXmlFile( filename, sharedfieldElement );
			}
			DbgPrintln( nSharedfieldList + " <sharedfield>s retrieved." );

			//. 共用列を処理
/*
			NodeList sharedcolumnList = databaseElement.getElementsByTagName( "sharedcolumn" );
			int nSharedcolumnList = sharedcolumnList.getLength();
			createFolder( repid + "/" + sharedcolumn_folder );
			for( int i = 0; i < nSharedcolumnList; i ++ ){
				Element sharedcolumnElement = ( Element )sharedcolumnList.item( i );
				String unid = getUnid( sharedcolumnElement );
				String filename = repid + "/" + sharedcolumn_folder + "/" + unid + ".xml";
				WriteElementToXmlFile( filename, sharedcolumnElement );
			}
			DbgPrintln( nSharedcolumnList + " <sharedcolumn>s retrieved." );
*/
			
			//. ビューを処理  (-> /(RepId)/views )
			NodeList viewList = databaseElement.getElementsByTagName( "view" );
			int nViewList = viewList.getLength();
			createFolder( repid + "/" + view_folder );
			for( int i = 0; i < nViewList; i ++ ){
				Element viewElement = ( Element )viewList.item( i );
				String unid = getUnid( viewElement );
				String filename = repid + "/" + view_folder + "/" + unid + ".xml";
				WriteElementToXmlFile( filename, viewElement );

				//. この後、共有要素をマージ
			}
			DbgPrintln( nViewList + " <view>s retrieved." );

			//. フォルダを処理  (-> /(RepId)/folders )
			NodeList folderList = databaseElement.getElementsByTagName( "folder" );
			int nFolderList = folderList.getLength();
			createFolder( repid + "/" + folder_folder );
			for( int i = 0; i < nFolderList; i ++ ){
				Element folderElement = ( Element )folderList.item( i );
				String unid = getUnid( folderElement );
				String filename = repid + "/" + folder_folder + "/" + unid + ".xml";
				WriteElementToXmlFile( filename, folderElement );
				
				//. この後、共有要素をマージ
			}
			DbgPrintln( nFolderList + " <folder>s retrieved." );

			//. イメージリソースを処理  (-> /(RepId)/htmls/imageresources )
			NodeList imageresourceList = databaseElement.getElementsByTagName( "imageresource" );
			int nImageresourceList = imageresourceList.getLength();
///			createFolder( repid + "/" + imageresource_folder );
			createFolder( repid + "/" + html_folder + "/" + imageresource_folder );
			for( int i = 0; i < nImageresourceList; i ++ ){
				Element imageresourceElement = ( Element )imageresourceList.item( i );
				String unid = getUnid( imageresourceElement );
				String filename = repid + "/" + imageresource_folder + "/" + unid + ".xml";
				WriteElementToXmlFile( filename, imageresourceElement );
				
				//. バイナリを取り出して保存する
				String name = imageresourceElement.getAttribute( "name" ); //. ファイル名
				if( name == null || name.length() == 0 ){
					name = imageresourceElement.getAttribute( "alias" );
				}
				if( name != null && name.length() > 0 ){
					int n = name.lastIndexOf( "." );
					if( n > -1 ){
						String ext = name.substring( n + 1 ).toLowerCase();
						try{
							NodeList imgList = imageresourceElement.getElementsByTagName( ext );
							if( imgList.getLength() > 0 ){
								Element imgElement = ( Element )imgList.item( 0 );
								String b64img = imgElement.getFirstChild().getNodeValue(); //. Base64 エンコード済みイメージ
								String imgurl = imageresourceUpload( repid, b64img, name );
///								System.out.println( "name = " + name + ", imgurl = " + imgurl );
							}
						}catch( Exception e ){
							e.printStackTrace();
						}
					}
				}
			}
			DbgPrintln( nImageresourceList + " <imageresource>s retrieved." );

			//. ファイルリソースを処理  (-> /(RepId)/fileresources )
			NodeList fileresourceList = databaseElement.getElementsByTagName( "fileresource" );
			int nFileresourceList = fileresourceList.getLength();
			createFolder( repid + "/" + fileresource_folder );
			for( int i = 0; i < nFileresourceList; i ++ ){
				Element fileresourceElement = ( Element )fileresourceList.item( i );
				String unid = getUnid( fileresourceElement );
				String filename = repid + "/" + fileresource_folder + "/" + unid + ".xml";
				WriteElementToXmlFile( filename, fileresourceElement );
			}
			DbgPrintln( nFileresourceList + " <fileresource>s retrieved." );

			//. 「データベースについて」文書を処理  (-> /(RepId)/helpabout )
			NodeList helpaboutdocumentList = databaseElement.getElementsByTagName( "helpaboutdocument" );
			int nHelpaboutdocumentList = helpaboutdocumentList.getLength();
			createFolder( repid + "/" + helpaboutdocument_folder );
			for( int i = 0; i < nHelpaboutdocumentList; i ++ ){
				Element helpaboutdocumentElement = ( Element )helpaboutdocumentList.item( i );
				String unid = getUnid( helpaboutdocumentElement );
				String filename = repid + "/" + helpaboutdocument_folder + "/" + unid + ".xml";
				WriteElementToXmlFile( filename, helpaboutdocumentElement );
			}
			DbgPrintln( nHelpaboutdocumentList + " <helpaboutdocument> retrieved." );
			
			//. 「データベースの使い方」文書を処理  (-> /(RepId)/helpusing )
			NodeList helpusingdocumentList = databaseElement.getElementsByTagName( "helpusingdocument" );
			int nHelpusingdocumentList = helpusingdocumentList.getLength();
			createFolder( repid + "/" + helpusingdocument_folder );
			for( int i = 0; i < nHelpusingdocumentList; i ++ ){
				Element helpusingdocumentElement = ( Element )helpusingdocumentList.item( i );
				String unid = getUnid( helpusingdocumentElement );
				String filename = repid + "/" + helpusingdocument_folder + "/" + unid + ".xml";
				WriteElementToXmlFile( filename, helpusingdocumentElement );
			}
			DbgPrintln( nHelpusingdocumentList + " <helpusingdocument> retrieved." );
			

			//. 文書を処理  (-> /(RepId)/docs )
			NodeList documentList = databaseElement.getElementsByTagName( "document" );
			int nDocumentList = documentList.getLength();
			createFolder( repid + "/" + doc_folder );
			for( int i = 0; i < nDocumentList; i ++ ){
				Element documentElement = ( Element )documentList.item( i );
				String unid = getUnid( documentElement );
				String filename = repid + "/" + doc_folder + "/" + unid + ".xml";
				WriteElementToXmlFile( filename, documentElement );
			}			
			DbgPrintln( nDocumentList + " <document>s retrieved.\n" );
			
/*
			//. ACL
			
			//. names.nsf を参照して、ACL 内のユーザー名を置換する
			View pview = null;
			try{
				Database nab = s.getDatabase( ddserver, ddfilepath );
				if( !nab.isOpen() ){
					nab.open();
				}
				pview = nab.getView( "People" );
			}catch( Exception e ){
			}

			NodeList aclList = databaseElement.getElementsByTagName( "acl" );
			Element aclElement = ( Element )aclList.item( 0 );
			NodeList aclentryList = databaseElement.getElementsByTagName( "aclentry" );
			int nAclentryList = aclentryList.getLength();
			createFolder( repid + "/" + acl_folder );
			for( int i = 0; i < nAclentryList; i ++ ){
				Element aclentryElement = ( Element )aclentryList.item( i );
				String entryname = aclentryElement.getAttribute( "name" );
				String name = entryname;

				if( pview != null ){
					lotus.domino.Document doc = pview.getFirstDocument();
					boolean b = true;
					while( doc != null && b ){
						//. FullName で比較して、一致していたら InternetAddress に置換
						String fullname = doc.getItemValueString( "FullName" );
						if( fullname.equalsIgnoreCase( entryname ) ){
							String name1 = doc.getItemValueString( "InternetAddress" );
							//. InternetAddress が定義されていない場合は置換しない
							if( name1 != null && name1.length() > 0 ){
								name = name1;
							}
							
							b = false;
						}
						
						doc = pview.getNextDocument( doc );
					}
				}
				
				aclentryElement.setAttribute( "name", name );
				
				String filename = repid + "/" + acl_folder + "/" + i + ".xml";
				WriteElementToXmlFile( filename, aclentryElement );
			}			
			DbgPrintln( nAclentryList + " <aclentry>s retrieved.\n" );
*/
			
			DbgPrintln( "STEP 1-1 done.\n" );

			
			//. Step 1-2 : 取り出した一部 XML 要素を変換
			
			//. ビュー／フォルダ内の共有要素を固定要素に変換
/*
			createFolder( repid + "/" + view_xml_folder );
			File view_dir = new File( repid + "/" + view_folder );
			if( view_dir.isDirectory() ){
				File[] view_files = view_dir.listFiles();
				for( int i = 0; i < view_files.length; i ++ ){
					DbgPrint( " XML for view #(" + ( i + 1 ) + "/" + view_files.length + ") is preparing.. " );
					
					//. オリジナルを退避
					String view_filename = view_files[i].getAbsolutePath();
					String view_filename2 = view_filename.replace( view_folder, view_xml_folder );
					binCopy( view_filename, view_filename2 );
					
					//. 共有列を展開
					replaceSharedElementView( repid, view_filename2 );

					//. <noteinfo>, <code>, <column>要素だけを取り出す
					String[] tags = { "noteinfo", "code", "column" };
					extractTag( view_filename2, tags );
					
					DbgPrintln( " done. " );
				}
			}

			createFolder( repid + "/" + folder_xml_folder );
			File folder_dir = new File( repid + "/" + folder_folder );
			if( folder_dir.isDirectory() ){
				File[] folder_files = folder_dir.listFiles();
				for( int i = 0; i < folder_files.length; i ++ ){
					DbgPrint( " XML for folder #(" + ( i + 1 ) + "/" + folder_files.length + ") is preparing.. " );
					
					//. オリジナルを退避
					String folder_filename = folder_files[i].getAbsolutePath();
					String folder_filename2 = folder_filename.replace( folder_folder, folder_xml_folder );
					binCopy( folder_filename, folder_filename2 );
					
					//. 共有列を展開
					replaceSharedElementView( repid, folder_filename2 );

					//. <noteinfo>, <code>, <column>要素だけを取り出す
					String[] tags = { "noteinfo", "code", "column" };
					extractTag( folder_filename2, tags ); //. これをやる前に sharedcolumn を統合する必要あり
					
					DbgPrintln( " done. " );
				}
			}
*/

			//. フォーム内の共有要素を固定要素に変換し、フィールド定義を正規化  (-> /(RepId)/form_xml/ )
			createFolder( repid + "/" + form_xml_folder );
			File form_dir = new File( repid + "/" + form_folder );
			if( form_dir.isDirectory() ){
				File[] form_files = form_dir.listFiles();
				for( int i = 0; i < form_files.length; i ++ ){
					DbgPrint( " XML for form #(" + ( i + 1 ) + "/" + form_files.length + ") is preparing.. " );
					
					//. オリジナルを退避
					String form_filename = form_files[i].getAbsolutePath();
					String form_filename2 = form_filename + ".xml";
					binCopy( form_filename, form_filename2 );
						
					//. サブフォームと共有フィールドを展開
					replaceSharedElementForm( repid, form_filename2 );
					
					//. 再修正
					String form_filename3 = form_filename.replace( form_folder, form_xml_folder );
///					String xml = readFileUTF8( form_filename2 );
///					writeFileUTF8( form_filename3, xml );
					binCopy( form_filename2, form_filename3 );
					
					//. 添付画像を取り除き、HTMLフォーマットを修正
///					Element root = getRootElement( xml );
					Element root = getRootElement( new File( form_filename3 ) );
					String unid = getUnid( root );
					//. K.Kimura 要調査(2013/Nov/07)
					omitExtra( repid, form_filename3, unid );
						
					//. <noteinfo>, <body>要素だけを取り出す
					String[] tags = { "noteinfo", "body" };
					//. K.Kimura 要調査(2013/Nov/07)
					extractTag( form_filename3, tags );
						
					DbgPrintln( " done." );
				}
			}
			
			DbgPrintln( "STEP 1-2 done.\n" );

			
			
			//. Step 1-3 : システムに必要な画像ファイルを用意
			
			//. DB, View, Document リンク  (-> /(RepId)/htmls/sys_imgs )
			createFolder( repid + "/" + html_folder + "/" + sys_imgs_folder );
			File sys_imgs_dir = new File( sys_imgs_folder );
			if( sys_imgs_dir.isDirectory() ){
				File[] sys_imgs_files = sys_imgs_dir.listFiles();
				for( int i = 0; i < sys_imgs_files.length; i ++ ){
					DbgPrintln( " System image file #(" + ( i + 1 ) + "/" + sys_imgs_files.length + ") is preparing.. " );
					String srcfilename = sys_imgs_files[i].getAbsolutePath();
					int n = srcfilename.lastIndexOf( sys_imgs_folder );
					String dstfilename = srcfilename.substring( 0, n ) + repid + "/" + html_folder + "/" + srcfilename.substring( n );
					binCopy( srcfilename, dstfilename );
				}
			}
			DbgPrintln( "STEP 1-3 done. System images ready.\n" );

			
			//. Step 1-4 : 各文書ごとに Form の定義 XML を修正  (-> /(RepId)/htmls/(UNID).xml )
			//. K.Kimura (2013/Nov/12) Step 3 へ移動
/*			
			File doc_dir = new File( repid + "/" + doc_folder );
			if( doc_dir.isDirectory() ){
				File[] doc_files = doc_dir.listFiles();
				for( int i = 0; i < doc_files.length; i ++ ){
					String doc_filename = doc_files[i].getAbsolutePath();
					DbgPrint( " XSL for document #(" + ( i + 1 ) + "/" + doc_files.length + ") is searching.. " );
					
					//. この後、文書データを直接編集するのでオリジナルを退避
					String doc_filename2 = doc_filename.replace( doc_folder, html_folder );
					binCopy( doc_filename, doc_filename2 );
					
					//. 再構成
					DbgPrint( " re-organizing.. " );
					String doc_xml = readFileUTF8( doc_filename2 );
					
					//. <?xml?> 宣言文を取り除く
					int n = doc_xml.indexOf( "?>" );
					if( n > -1 ){
						doc_xml = doc_xml.substring( n + 2 );
					}

					doc_xml = swapNotesLinks( repid, doc_xml );

					while( doc_xml.indexOf( "<notesbitmap" ) > -1 ){
						//. ペーストされた画像は切り取る
						doc_xml = omitElement( doc_xml, "notesbitmap" );
					}
					
					doc_xml = doc_xml.replaceAll( "<par ", "<p " );
					doc_xml = doc_xml.replaceAll( "</par>", "</p>" );
					writeFileUTF8( doc_filename2, doc_xml );

					int n1 = doc_xml.indexOf( "unid='" );
					int n2 = doc_xml.indexOf( "'", n1 + 6 );
					if( -1 < n1 && n1 < n2 ){
						String unid = doc_xml.substring( n1 + 6, n2 );
						
						omitAttachment( repid, doc_filename2 );
						omitExtra( repid, doc_filename2, unid );
					}
					
					//. セクション
					replaceSection( doc_filename2 );
					
					//. name 属性の item の名前をインターネットアドレスに変換
//					if( pview != null ){
//						replaceName( doc_filename2, pview );
//					}

					DbgPrintln( " done. -> " + doc_filename2 );
				}				
			}
*/			
			DbgPrintln( "STEP 1-4 done. XMLs for all documents generated.\n" );

			//. Step 1-5 : 各フォームごとに定義 XML を修正  (-> /(RepId)/htmls/(UNID).dxl )
			//. K.Kimura (2013/Nov/12) Step 3 へ移動
/*
			File form_xml_dir = new File( repid + "/" + form_xml_folder );
			if( form_xml_dir.isDirectory() ){
				File[] form_files = form_xml_dir.listFiles();
				for( int i = 0; i < form_files.length; i ++ ){
					String form_filename = form_files[i].getAbsolutePath();
					DbgPrint( " DXL for form #(" + ( i + 1 ) + "/" + form_files.length + ") is updating.. " );
					
					//. この後、フォームデータを直接編集するのでオリジナルを退避
					String form_filename2 = form_filename.replace( form_xml_folder, html_folder );
					form_filename2 = form_filename2.replace( ".xml", ".dxl" );
					
					//. フォームの DXL を取得
					String form_dxl = readFileUTF8( form_filename );
					
					//. DXL を整形
					int n1 = form_dxl.indexOf( "<field " );
					while( n1 > -1 ){
						int n2 = form_dxl.indexOf( ">", n1 + 1 );
						if( n1 < n2 ){
							String s1 = form_dxl.substring( 0, n2 );
							String s2 = form_dxl.substring( n2 );
							form_dxl = s1 + "/" + s2;
						}else{
							n2 = form_dxl.length();
						}
						
						n1 = form_dxl.indexOf( "<field ", n2 + 1 );
					}
//					form_dxl = form_dxl.replaceAll( "(<field[^>]*)>", "$1/>" );
					form_dxl = form_dxl.replaceAll( "</field[^>]*>", "" );
					form_dxl = form_dxl.replaceAll( "<compositedata[^<]*</compositedata>", "" );
					form_dxl = form_dxl.replaceAll( "<compositedata[^>]*>", "" );
					form_dxl = form_dxl.replaceAll( "</compositedata>", "" );

					//. XSL 内のタグを調整
					form_dxl = swapNotesLinks( repid, form_dxl );

					while( form_dxl.indexOf( "<notesbitmap" ) > -1 ){
						//. ペーストされた画像は切り取る
						form_dxl = omitElement( form_dxl, "notesbitmap" );
					}
					
					form_dxl = form_dxl.replaceAll( "<par ", "<p " );
					form_dxl = form_dxl.replaceAll( "</par>", "</p>" );

					//. 書き戻す
					writeFileUTF8( form_filename2, form_dxl );
					
					DbgPrintln( " done. " );
				}
			}
*/
			DbgPrintln( "STEP 1-5 done. XMLs for all forms generated.\n" );
			
			
			//. Step 1-6 : 「データベースの使い方」、「データベースについて」の XML/DXL ファイルを用意する
			//. K.Kimura 要ロジック変更（ACL未チェックでも用意する）
			
			//. このデータベースに対する編集者以上の権限を持っているユーザー／グループを調べる
			String designers0 = "";
			String[] designers = null;
			File acl_dir = new File( repid + "/" + acl_folder );
			if( acl_dir.isDirectory() ){
				boolean b = true;
				File[] acl_files = acl_dir.listFiles();
				for( int i = 0; i < acl_files.length && b; i ++ ){
///					String aclentry_xml = readFileUTF8( acl_files[i].getAbsolutePath() );
///					Element aclentryElement = getRootElement( aclentry_xml );
					Element aclentryElement = getRootElement( new File( acl_files[i].getAbsolutePath() ) );
					String acl_level = aclentryElement.getAttribute( "level" );
					if( acl_level.equals( "manager" ) || acl_level.equals( "designer" ) ){
						String acl_name = aclentryElement.getAttribute( "name" );
						if( acl_name.equalsIgnoreCase( "-default-" ) ){
							designers0 = "";
							b = false;
						}else{
							if( designers0.length() == 0 ){
								designers0 = acl_name;
							}else{
								designers0 += ( "," + acl_name );
							}
						}
					}
				}
			}
			if( designers0.length() > 0 ){
				designers = designers0.split( "," );
			}
			
			//. 「データベースの使い方」 の XML/DXL ファイルを用意する (-> /(RepId)/htmls/helpusing )
			createFolder( repid + "/" + html_folder + "/" + helpusingdocument_folder );
			File helpusingdocument_dir = new File( repid + "/" + helpusingdocument_folder );
			if( helpusingdocument_dir.isDirectory() ){
				File[] helpusingdocuments = helpusingdocument_dir.listFiles();
				for( int i = 0; i < helpusingdocuments.length; i ++ ){
					String helpusingdocument_filename = helpusingdocuments[i].getAbsolutePath();
					DbgPrint( " DXL for helpusingdocument #(" + helpusingdocument_filename + ") is updating.. " );
					
					//. XML を整形し、DXL を生成
					int r = helpToXMLDXL( helpusingdocument_filename, designers, repid );
					
					DbgPrintln( " done. " );
				}
			}

			//. 「データベースについて」 の XML/DXL ファイルを用意する(-> /(RepId)/htmls/helpabout )
			createFolder( repid + "/" + html_folder + "/" + helpaboutdocument_folder );
			File helpaboutdocument_dir = new File( repid + "/" + helpaboutdocument_folder );
			if( helpaboutdocument_dir.isDirectory() ){
				File[] helpaboutdocuments = helpaboutdocument_dir.listFiles();
				for( int i = 0; i < helpaboutdocuments.length; i ++ ){
					String helpaboutdocument_filename = helpaboutdocuments[i].getAbsolutePath();
					DbgPrint( " DXL for helpaboutdocument #(" + helpaboutdocument_filename + ") is updating.. " );
					
					//. XML を整形し、DXL を生成
					int r = helpToXMLDXL( helpaboutdocument_filename, designers, repid );
					
					DbgPrintln( " done. " );
				}
			}

			DbgPrintln( "STEP 1-6 done. all help-related documents generated.\n" );

			//. Step 1-7 : システムに必要な XML ファイルを用意
			
			//. View および View Navi の XML ファイルを作成 (-> /(RepId)/docview/ )
			createFolder( repid + "/" + doc_in_view_folder );
			HashMap<String, String[]> viewmap = new HashMap<String, String[]>();
			Vector views = db.getViews(); //. この方法だとフォルダも含まれる
			for( int i = 0; i < views.size(); i ++ ){
				View view = ( View )views.elementAt( i );
				String viewname = ( String )( view.getName() );
				String view_unid = view.getUniversalID();
				
				//. 隠しビューでも表示するかどうかを判断
				if( hidden_view || ( !viewname.startsWith( "(" ) && !viewname.endsWith( ")" ) ) ){ //. 隠しビューは対象外とする
					String view_folder = ( view.isFolder() ) ? "fold" : "view";
					String[] values = { viewname, view_folder };
					viewmap.put( view_unid, values );
					
					//. 文書一覧（ある文書がどのビューに含まれるか、という観点と、あるビューにどの文書が含まれるか、という２つの観点）
					String docs_in_view = "";
					lotus.domino.Document ndoc = view.getFirstDocument();
					while( ndoc != null ){
						String doc_unid = ndoc.getUniversalID();
						
						//. このビューに含まれる文書の一覧
						
						//. 上の条件だとダブりを認めないため、複数カテゴリを再現できない
/*
						if( doc_unid.length() > 0 ){
							if( docs_in_view.length() == 0 ){
								docs_in_view = doc_unid;
							}else{
								docs_in_view += ( "," + doc_unid );
							}
						}
*/
						
						//. この文書がどのビューに含まれているか？ (-> /(RepId)/docview/(UNID).csv )
						String filename = repid + "/" + doc_in_view_folder + "/" + doc_unid + ".csv";
						appendTextToFile( filename, view_unid + "," );
						
						//. K.Kimura 2013/Nov/08  この文書の親文書を調べておく必要は？ この上でやるべき？
						
						
						ndoc = view.getNextDocument( ndoc );
					}
					
					//. このビューの文書一覧 XML を作成
/*
					String xml = "\n\n<database id='" + view_unid + "' view_name='" + viewname + "' view_unid='" + view_unid + "'>\n";
					String[] docids = docs_in_view.split( "," );
					for( int j = 0; j < docids.length; j ++ ){
						String filename = repid + "/" + html_folder + "/" + docids[j] + ".xml";
						String doc_dxl = readFileUTF8( filename );
						int n = doc_dxl.indexOf( "<document", 1 );
						doc_dxl = doc_dxl.substring( n );     //. <document form='**' (parent='+++' response='true')> ～ </document>

						try{
							Element documentElement = getRootElement( "<?xml version='1.0'?>\n" + doc_dxl ); //. K.Kimura(Exception! 2011/Apr/25)
							String parent = null;
							try{
								parent = documentElement.getAttribute( "parent" );
							}catch( Exception e ){
							}
							
							if( parent != null && parent.length() > 0 ){
								//. 親文書を探す
								int cnt = 1, n1 = -1, n2;
								n = xml.indexOf( " unid='" + parent + "'" );
								if( -1 < n ){
									while( cnt > 0 ){
										n1 = xml.indexOf( "</document>", n ); //. 挿入直前位置の候補
										n2 = xml.indexOf( "<document ", n );
										
										if( n2 == -1 || n1 < n2 ){
											cnt --;
											n = n1 + 1;
										}else if( n1 > n2 ){
											cnt ++;
											n = n2 + 1;
										}
									}
									
									String s1 = xml.substring( 0, n1 );
									String s2 = xml.substring( n1 );
									
									//. document の DXL そのものではなく、<document> 要素だけを追加する
//									xml = s1 + doc_dxl + s2;
									String form = documentElement.getAttribute( "form" );
									String doc = "<document form='" + form + "' repid='" + repid + "' unid='" + docids[j] + "' parent='" + parent + "'></document>\n";
									xml = s1 + doc + s2;
								}
							}else{
//								xml += doc_dxl;
								String form = documentElement.getAttribute( "form" );
								String doc = "<document form='" + form + "' repid='" + repid + "' unid='" + docids[j] + "'></document>\n";
								xml += doc;
							}
						}catch( Exception e ){
							e.printStackTrace();
						}
					}
					xml += "</database>\n";
										
					writeFileUTF8( repid + "/" + html_folder + "/" + view_unid + ".xml", xml );
*/
				}				
			}
			DbgPrintln( "XMLs for views/folders generated.\n" );
			

			//. CSV ファイルからビュー一覧のダブりを排除　　-> 複数値を持つカテゴリに対応できなくなるので、しない方がいいのかも・・でも CSV ファイルを使う？？ K.Kimura
			//. K.Kimura ?? 2013/Nov/08
			File csv_dir = new File( repid + "/" + doc_in_view_folder );
			if( csv_dir.isDirectory() ){
				File[] csv_files = csv_dir.listFiles();
				for( int i = 0; i < csv_files.length; i ++ ){
					String csvfilename = csv_files[i].getAbsolutePath();
					String viewlist = "", csv = readFileUTF8( csvfilename );
					String[] stmp = csv.split( "," );
					for( int j = 0; j < stmp.length; j ++ ){
						if( stmp[j].length() > 0 && views.indexOf( stmp[j] ) == -1 ){
							if( viewlist.length() == 0 ){
								viewlist = stmp[j];
							}else{
								viewlist += ( "," + stmp[j] );
							}
						}
					}
					writeFileUTF8( csvfilename, viewlist );
				}
			}
			DbgPrintln( "CSVs for documents & view/folders relationship generated.\n" );
			
			//. K.Kimura 2013/Nov/08 要調査  (-> /(RepId)/htmls/view_list.xml )
			//.  どのようなビュー／フォルダがあるかの一覧を作るだけでよい？
			//. views_list.xml
			String views_xml = /*"<!--\n"
					+ "<?xml version='1.0' ?>\n"
					+ "<?xml-stylesheet href='views_list.xsl' type='text/xsl'?>\n"
					+ "-->\n"
					+ */"<database view_start='1' view_end='" + viewmap.size() + "'>\n";
			for( Iterator it = viewmap.entrySet().iterator(); it.hasNext(); ){
				Map.Entry entry = ( Map.Entry )it.next();
				String view_unid = ( String )entry.getKey();
				String[] values = ( String[] )entry.getValue();
				String view_name = values[0];
				String view_folder = values[1];

				views_xml += ( "<view unid='" + view_unid + "' name='" + view_name + "' kind='" + view_folder + "'/>\n" );
			}
			views_xml += ( "</database>\n" );
			
			String filename = repid + "/" + html_folder + "/views_list.xml";
			writeFileUTF8( filename, views_xml );

			DbgPrintln( "XMLs for view list generated.\n" );
		
			//. index.htm(Frameset) の HTML ファイル作成
/*
			String html = "<html>\n<head>\n"
					+ "<meta http-equiv='Content-Type' content='text/html; charset=UTF-8' />\n"
					+ "<title>" + db.getTitle() + "</title>\n"
					
					+ "<script type='text/javascript'>\n"
					+ "<!--\n"
					+ "if( location.search.length > 1 ){\n"
					+ "  var m = location.search.substr( 1 ).split( '&' );\n"
					+ "  for( idx in m ){\n"
					+ "    var q = m[idx].split( '=' );\n"
					+ "    if( q[0] == 'm' && q[1] == '1' ){\n"
					+ "      location.href = '/getdoc?id=" + repid + "-views_list.xml&m=1';\n"
					+ "    }\n"
					+ "  }\n"
					+ "}\n"
					+ "//-->\n"
					+ "</script>\n"

					+ "<link rel='shortcut icon' href='/getdxl?id=" + repid + "-imgs/dbicon.gif' type='image/gif' />\n"
					+ "<link rel='icon' href='/getdxl?id=" + repid + "-imgs/dbicon.gif' type='image/gif' />\n"

					+ "</head>\n"
					+ "<frameset cols='25%,*'>\n"
					+ "<frame name='navi' src='views_list.xml'/>\n"
					+ "<frameset rows='70%,*'>\n"
					+ "<frame name='view' src=''/>\n"
					+ "<frame name='preview' src=''/>\n"
					+ "</frameset>\n"
					+ "</frameset>\n"
					+ "</html>\n";
			filename = repid + "/" + html_folder + "/" + "index" + ".htm";
			writeFileUTF8( filename, html );
		
			DbgPrintln( "STEP 1-7 done. Other XMLs ready.\n" );
*/			
						
			DbgPrintln( "ExportDb finished.\n" );
		}catch( Exception e ){
			e.printStackTrace();
		}
		
		return repid;
	}
	
	
	public static void GenerateXSLs( String repid ){
		try{
			//. 初期設定読み込み
//			readParam( "n2h.ini" );
			
			//. Step 2-1 : 各フォームの XSL を生成
			
			//. サーバー側での処理に変更したので、こちらでは作らない
			//. と思ったけど、この後のビューの処理の中でフォームのXMLを参照するので、XMLパースが通る程度の整備は必要

			//. フォームの格納先はオリジナルではなく、共有要素を展開したほう
/*			File form_dir = new File( repid + "/" + form_xml_folder );
			if( form_dir.isDirectory() ){
				File[] form_files = form_dir.listFiles();
				for( int i = 0; i < form_files.length; i ++ ){
					String form_filename = form_files[i].getAbsolutePath();
					DbgPrint( " XSL for form #(" + ( i + 1 ) + "/" + form_files.length + ") is preparing.. " );
					
					//. フォームが見つかった
					String form_dxl = readFileUTF8( form_filename );								
					form_dxl = form_dxl.replaceAll( "<br>", "<br/>" );
					Element formElement = getRootElement( form_dxl ); // <form>

					@SuppressWarnings("unused")
					String form_unid = getUnid( formElement );
					generateFormXsl( repid, form_dxl );

					DbgPrintln( " done." );
				}
			}
*/			DbgPrintln( "STEP 2-1 skipped.\n" );			

			
			//. Step 2-2 : 各ビュー／フォルダごとに定義 XML を修正して XSL を作成
			//. K.Kimura 2013/Nov/08 要調査 
/*
			File view_dir = new File( repid + "/" + view_xml_folder );
			if( view_dir.isDirectory() ){
				File[] view_files = view_dir.listFiles();
				for( int i = 0; i < view_files.length; i ++ ){
					String view_filename = view_files[i].getAbsolutePath();
					DbgPrint( " XSL for view #(" + ( i + 1 ) + "/" + view_files.length + ") is searching.. <" + view_filename + "> " );
					
					//. 再構成
					DbgPrint( " re-organizing.. " );
					String view_xml = readFileUTF8( view_filename );
					
					//. K.Kimura 2013/Nov/08 要調査
					generateViewXsl( repid, view_xml );

					DbgPrintln( " done." );
				}
			}

			File folder_dir = new File( repid + "/" + folder_xml_folder );
			if( folder_dir.isDirectory() ){
				File[] folder_files = folder_dir.listFiles();
				for( int i = 0; i < folder_files.length; i ++ ){
					String folder_filename = folder_files[i].getAbsolutePath();
					DbgPrint( " XSL for folder #(" + ( i + 1 ) + "/" + folder_files.length + ") is searching.. " );
					
					//. 再構成
					DbgPrint( " re-organizing.. " );
					String folder_xml = readFileUTF8( folder_filename );
					
					//. K.Kimura 2013/Nov/08 要調査
					generateViewXsl( repid, folder_xml );

					DbgPrintln( " done." );
				}
			}
			
			DbgPrintln( "STEP 2-2 done.\n" );
*/
			
			//. Step 2-3 : views_list.xml の XSL を作成

			//. K.Kimura 2013/Nov/08 要調査
/*
			//. DB名
///			String db_dxl = readFileUTF8( repid + "/" + db_folder + "/" + repid + ".xml" );								
///			Element dbElement = getRootElement( db_dxl ); // <db>
			Element dbElement = getRootElement( new File( repid + "/" + db_folder + "/" + repid + ".xml" ) ); // <db>
			String dbname = dbElement.getAttribute( "title" );
			
			//. ヘルプ関連(UNID)
			String helpusing_unid = "", helpabout_unid = "";
			File helpusingdocument_dir = new File( repid + "/" + helpusingdocument_folder );
			if( helpusingdocument_dir.isDirectory() ){
				File[] helpusingdocuments = helpusingdocument_dir.listFiles();
				for( int i = 0; i < helpusingdocuments.length; i ++ ){
					String helpusingdocument_filename = helpusingdocuments[i].getAbsolutePath();
					if( helpusingdocument_filename.endsWith( ".xml" ) ){
///						String helpusing_dxl = readFileUTF8( helpusingdocument_filename );
///						Element helpusingElement = getRootElement( helpusing_dxl );
						Element helpusingElement = getRootElement( new File( helpusingdocument_filename ) );
						helpusing_unid = getUnid( helpusingElement );
					}
				}
			}
			File helpaboutdocument_dir = new File( repid + "/" + helpaboutdocument_folder );
			if( helpaboutdocument_dir.isDirectory() ){
				File[] helpaboutdocuments = helpaboutdocument_dir.listFiles();
				for( int i = 0; i < helpaboutdocuments.length; i ++ ){
					String helpaboutdocument_filename = helpaboutdocuments[i].getAbsolutePath();
					if( helpaboutdocument_filename.endsWith( ".xml" ) ){
///						String helpabout_dxl = readFileUTF8( helpaboutdocument_filename );
///						Element helpaboutElement = getRootElement( helpabout_dxl );
						Element helpaboutElement = getRootElement( new File( helpaboutdocument_filename ) );
						helpabout_unid = getUnid( helpaboutElement );
					}
				}
			}
			

			//. PC 用 XSL
			String list_body = 
				"<a target='_top' href='/'>&lt;&lt;</a> \n"
//				"<ul class='dropdown'>\n"
//						+ "<li><a href='#'>&lt;&lt;</a>\n"
//						+ "<ul class='sub_menu'>\n"
//						+ "<li><a target='_top' href='/'>Top</a></li>\n"
//						+ "<li><a target='preview' href='" + helpusingdocument_folder + "/" + helpusing_unid + ".xml'>Help Using</a></li>\n"
//						+ "<li><a target='preview' href='" + helpaboutdocument_folder + "/" + helpabout_unid + ".xml'>Help About</a></li>\n"
//						+ "</ul>\n"
//						+ "</li>\n"
//						+ "</ul>\n"
				+ "<img width='32' height='32' src='" + picture_folder + "/" + sys_imgs_dbicon + "'/> \n"
				+ "<b>" + dbname + "</b><p/>\n"
				+ "<table border='0'>\n"
				+ "<xsl:apply-templates select='/database/view'/>\n"
				+ "</table>\n";
			
			//. 検索フィールド
			list_body += "<hr/>\n<form name='search' method='post' target='view' action='/search'>\n"
				+ "<input type='text' name='key'/>\n"
				+ "<input type='hidden' name='repid' value='" + repid + "'/>\n"
				+ "<input type='hidden' name='bgcolor' value='" + view_list_bgcolor + "'/>\n"
				+ "</form>\n";
			
			//. ヘルプへのリンク
			list_body += "<div align='right'>\n";
			if( helpusing_unid != null && helpusing_unid.length() > 0 ){
				list_body += "<a target='preview' href='" + helpusingdocument_folder + "/" + helpusing_unid + ".xml'>" + "このデータベースの使い方" + "</a><br/>\n";
			}
			if( helpabout_unid != null && helpabout_unid.length() > 0 ){
				list_body += "<a target='preview' href='" + helpaboutdocument_folder + "/" + helpabout_unid + ".xml'>" + "このデータベースについて" + "</a><br/>\n";
			}
			
			//. ACL 参照
///			list_body += "<a target='preview' href='" + helpaboutdocument_folder + "/" + helpabout_unid + ".xml'>" + rb.getString( "help.about" ) + "</a>\n";
			list_body += "<a href='javascript:window.open( \"/getaclpage?repid=" + repid + "\", null, \"width=400,height=300,menubar=no,toolbar=no,scrollbars=yes\" );'>ACL</a>\n";
			
			list_body += "</div>\n";
			
			String list_xsl = "<xsl:template match='/'>\n"
				+ "<html>\n"
				+ "<head>\n"
				+ "<title>views_list</title>\n"
				+ "<link rel='shortcut icon' type='image/gif' href='" + picture_folder + "/" + sys_imgs_dbicon + "'/>\n"

				+ "<script src='js/jquery.js'></script>\n"
				+ "<script src='js/dropdownPlain.js'></script>\n"
//				+ "<script type='text/javascript'>\n"
//				+ "$( function(){\n"
//                + "  $('#navigation a').animateToSelector( {\n"
//                + "    selectors: ['#navigation a:hover'],\n"
//                + "    properties: [\n"
//                + "      'background-color',\n"
//                + "      'padding-left',\n"
//                + "      'color'\n"
//                + "    ],\n"
//                + "    duration:600,\n"
//                + "    events: ['mouseover', 'mouseout']\n"
//                + "  });\n"
//				+ "});\n"
//				+ "</script>\n"

				+ "<style type='text/css'>\n"
				+ "a{\n"
				+ "  text-decoration: none;\n"
				+ "}\n"
//				+ "#navigation{\n"
//				+ "  margin: 0;\n"
//				+ "  padding: 0;\n"
//				+ "  list-style: none;\n"
//				+ "  font-size: 1.4em;\n"
//				+ "  width: 250px;\n"
//				+ "}\n"
//				+ "#navigation a{\n"
//				+ "  display: block;\n"
//				+ "  color: #fff;\n"
//				+ "  text-decoration: none;\n"
//				+ "  padding: 5x 10px;\n"
//				+ "  cursor: pointer;\n"
//				+ "}\n"
//				+ "#navigation a:hover{\n"
//				+ "  padding-left: 30px;\n"
//				+ "  background-color: #bdd704;\n"
//				+ "  color: #222;\n"
//				+ "}\n"
//				+ "#navigation li{"
//				+ "  margin: 0;\n"
//				+ "  width: 100%;\n"
//				+ "}\n"
				+ "</style>\n"
				+ "</head>\n"
				+ "<body bgcolor='" + view_list_bgcolor + "'>\n";

			list_xsl += "\n"
				+ list_body + "\n"
				+ "</body>\n"
				+ "</html>\n"
				+ "</xsl:template>\n\n";
			
			list_xsl += "<xsl:template match='/database/view'>"
				+ "<tr>\n"
				+ "<td>\n"
				+ "<img>\n"
				+ "<xsl:attribute name='src'>"
				+ "/getdxl?id=" + repid + "-" + sys_imgs_folder + "/<xsl:value-of select='./@kind'/>.gif"
				+ "</xsl:attribute>\n"
				+ "</img>\n"
				+ "<a>\n"
				+ "<xsl:attribute name='target'>view</xsl:attribute>\n"
				+ "<xsl:attribute name='href'>"
				+ "/getdoc?id=" + repid + "-"  //. 追加
				+ "<xsl:value-of select='./@unid'/>.xml"
				+ "</xsl:attribute>\n"
				+ "<xsl:value-of select='./@name'/>"
				+ "</a>\n"
				+ "</td>\n"
				+ "</tr>\n"
				+ "</xsl:template>\n";

			list_xsl = "<?xml version='1.0' ?>\n"
					+ "<xsl:stylesheet version='1.0' xmlns:xsl='http://www.w3.org/1999/XSL/Transform'>\n"
					+ "<xsl:output method='html' encoding='UTF-8'/>\n"
					+ list_xsl
					+ "</xsl:stylesheet>\n";

			//. PC 用 XSL ファイルを保存			
			String filename = repid + "/" + html_folder + "/views_list.xsl";
			writeFileUTF8( filename, list_xsl );

			
			//. モバイル用 XSL
			String m_list_body = "<div data-role='page' id='top'>\n"
				+ "<div data-role='header'>\n"
				
				+ "<a href='#' data-rel='back'>&lt;&lt;</a>\n" //. 「戻る」
				
				+ "<h1>" + dbname + "</h1>\n"
//				+ "<img src='" + picture_folder + "/" + sys_imgs_dbicon + "'/><p/>\n"
				+ "</div>\n"
				+ "<div data-role='content'>\n"
//				+ "<ol data-role='listview' data-inset='true'>\n"
				+ "<ul data-filter='true' data-role='listview' data-inset='true'>\n"
				+ "<xsl:apply-templates select='/database/view'/>\n"
//				+ "</ol>\n"
				+ "</ul>\n"
				+ "</div>\n";
			
			//. 検索フィールド
			m_list_body += "<div data-role='footer'>\n"
				+ "<form name='search' method='post' action='/search'>\n"
				+ "<input type='text' name='key'/>\n"
				+ "<input type='hidden' name='m' value='1'/>\n"
				+ "<input type='hidden' name='repid' value='" + repid + "'/>\n"
				+ "</form>\n"
				
				+ "<div align='right'>\n"
				+ "<a href='" + helpusingdocument_folder + "/" + helpusing_unid + ".xml'>使い方</a><br/>\n"
				+ "<a href='" + helpaboutdocument_folder + "/" + helpabout_unid + ".xml'>このデータベースについて</a>\n"
				+ "</div>\n"

				+ "</div>\n"
				+ "</div>\n";
			
			String m_list_xsl = "<xsl:template match='/'>\n"
				+ "<html>\n"
				+ "<head>\n"
				+ "<title>views_list</title>\n"
				+ "<meta name='viewport' content='width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no'/>\n"
				+ "<meta name='apple-mobile-web-app-capable' content='yes' /> \n"
				+ "<script type='text/javascript'>\n"
				+ "window.onload = function(){\n"
				+ "  setTimeout( scrollTo, 100, 0, 1 );\n"
				+ "}\n"
				+ "</script>\n"
				+ "<link href='js/jquery.mobile-1.0a4.1.min.css' rel='stylesheet'></link>\n"
				+ "<script type='text/javascript' src='js/jquery.js'></script>\n"
				
				+ "<script type='text/javascript'>\n"
				+ "$( document ).bind( 'mobileinit', function(){\n"
				+ "//$.mobile.ajaxLinksEnabled = false;\n"
				+ "  $.mobile.ajaxFormsEnabled = false;\n"
				+ "} );\n"
				+ "</script>\n"
				
				+ "<script type='text/javascript' src='js/jquery.mobile-1.0a4.1.min.js'></script>\n"
//				+ "<title>views_list</title>\n" //. ここでも表示された（<title>の位置には関係なさそう）
				+ "<link rel='shortcut icon' type='image/gif' href='" + picture_folder + "/" + sys_imgs_dbicon + "'/>\n"
				+ "</head>\n"
				+ "<body>\n";

			m_list_xsl += "\n"
				+ m_list_body + "\n"
				+ "</body>\n"
				+ "</html>\n"
				+ "</xsl:template>\n\n";
			
			m_list_xsl += "<xsl:template match='/database/view'>"
//				+ "<li dojoType='dojox.mobile.ListItem' icon='/getdxl?id=" + repid + "-" + sys_imgs_folder + "/" + sys_imgs_view + "' transition='slide'>\n"
				+ "<li>\n"
				+ "<a>\n"
				+ "<xsl:attribute name='href'>"
				+ "/getdoc?id=" + repid + "-"  //. 追加
				+ "<xsl:value-of select='./@unid'/>.xml&amp;m=1"
				+ "</xsl:attribute>\n"

				+ "<xsl:attribute name='rel'>external</xsl:attribute>\n"
				
				+ "<xsl:value-of select='./@name'/>"
				+ "</a>"
				+ "</li>\n"
				+ "</xsl:template>\n";

			m_list_xsl = "<?xml version='1.0' ?>\n"
					+ "<xsl:stylesheet version='1.0' xmlns:xsl='http://www.w3.org/1999/XSL/Transform'>\n"
					+ "<xsl:output method='html' encoding='UTF-8'/>\n"
					+ m_list_xsl
					+ "</xsl:stylesheet>\n";

			//. モバイル用 XSL ファイルを保存			
			filename = repid + "/" + html_folder + "/mviews_list.xsl";
			writeFileUTF8( filename, m_list_xsl );
			
			DbgPrintln( "STEP 2-3 done.\n" );	
*/

			//. K.Kimura 2013/Nov/11 XSL トランスフォームとカテゴリ作成が必要
/*
			//. Step 2-4 : XSL 作成
			File doc_dir = new File( repid + "/" + html_folder );
			File[] form_files = doc_dir.listFiles();
			for( int j = 0; j < form_files.length; j ++ ){
				String formfilename = form_files[j].getAbsolutePath();
				if( formfilename.endsWith( ".dxl" ) ){
					String form_xsl = readFileUTF8( formfilename );
//					Element docElement = getRootElement( new File( docfilename ) );
					Element formElement = getRootElement( form_xsl );
					
					//. ボディ（<richtext>～</richtext>部分を取り出す）
					String form_body = "";
					int n1 = form_xsl.indexOf( "<richtext" );
					int n2 = form_xsl.lastIndexOf( "</richtext>" );
					if( -1 < n1 && n1 < n2 ){
						form_body = form_xsl.substring( n1, n2 + 11 );

						//. XSL のテンプレート用修正
						n1 = form_body.indexOf( "<field " );
						while( n1 > -1 ){
							n2 = form_body.indexOf( ">", n1 + 1 ); //. 実際は "/>" のはず
							if( n2 > n1 ){
								String field_name = "", field_type = "";
								int n01 = form_body.indexOf( "name='", n1 + 1 );
								if( n01 > -1 ){
									int n02 = form_body.indexOf( "'", n01 + 6 );
									if( n02 > n01 ){
										field_name = form_body.substring( n01 + 6, n02 );
									}
								}
								
								n01 = form_body.indexOf( "type='", n1 + 1 );
								if( n01 > -1 ){
									int n02 = form_body.indexOf( "'", n01 + 6 );
									if( n02 > n01 ){
										field_type = form_body.substring( n01 + 6, n02 );
									}
								}
								
								String xsl_type = ( field_type.equals( "text" ) ) ? "value-of" : "copy-of";

								String s1 = form_body.substring( 0, n1 );
								String s3 = form_body.substring( n2 + 1 );
								String s2 = "<xsl:" + xsl_type + " select='/document/item[@name=\"" + field_name + "\"]'/>";
															
								form_body = s1 + s2 + s3;
								
								n1 = form_body.indexOf( "<field " );
							}
						}
						
						form_xsl = "<xsl:template match='/'>\n"
								+ "<html>\n"
								+ "<head>\n"
								+ "<title><xsl:value-of select='/document/noteinfo/@unid'/></title>\n";

						form_xsl += "</head>\n"
								+ "<body>";

						form_xsl += form_body;

						form_xsl += "</body>\n"
								+ "</html>\n"
								+ "</xsl:template>\n\n";

						form_xsl = "<?xml version='1.0' ?>\n"
								+ "<xsl:stylesheet version='1.0' xmlns:xsl='http://www.w3.org/1999/XSL/Transform'>\n"
								+ "<xsl:output method='html' encoding='UTF-8'/>\n"
								+ form_xsl
								+ "</xsl:stylesheet>\n";

						//. 保存
						String xslfilename = formfilename.replace( ".dxl", ".xsl" );
						writeFileUTF8( xslfilename, form_xsl );
					}
				}
			}


			DbgPrintln( "STEP 2-4 done.\n" );

			//. Step 2-5 : XSL トランスフォーム（これは最後に）
//			File doc_dir = new File( repid + "/" + html_folder );
			if( doc_dir.isDirectory() ){
				File[] doc_files = doc_dir.listFiles();
				for( int i = 0; i < doc_files.length; i ++ ){
					String docfilename = doc_files[i].getAbsolutePath();
					if( docfilename.endsWith( ".xml" ) ){
						String doc_xml = readFileUTF8( docfilename );
//						Element docElement = getRootElement( new File( docfilename ) );
						Element docElement = getRootElement( doc_xml );
						String doc_form = docElement.getAttribute( "form" );
						
						form_files = doc_dir.listFiles();
						for( int j = 0; j < form_files.length; j ++ ){
							String formfilename = form_files[j].getAbsolutePath();
							if( formfilename.endsWith( ".dxl" ) ){
								String form_xml = readFileUTF8( formfilename );
//								Element docElement = getRootElement( new File( docfilename ) );
								Element formElement = getRootElement( form_xml );
								String formname = formElement.getAttribute( "name" );
								if( doc_form.equals( formname ) ){
									//. 見つけた！
									String xslfilename = formfilename.replace( ".dxl", ".xsl" );
									try{
										String htmlfilename = docfilename.replace( ".xml", ".htm" );
										StreamSource xmlSrc = new StreamSource( docfilename );
										StreamSource xslSrc = new StreamSource( xslfilename );
										StreamResult htmlDst = new StreamResult( new FileOutputStream( htmlfilename ) );
										
										TransformerFactory factory = TransformerFactory.newInstance();
										Transformer transformer = factory.newTransformer( xslSrc );
										transformer.transform( xmlSrc, htmlDst );
									}catch( Exception e ){
										e.printStackTrace();
									}
								}
							}
						}
					}
				}
			}

			DbgPrintln( "STEP 2-5 done.\n" );
*/
			DbgPrintln( "GenerateXSLs finished.\n" );
		}catch( Exception e ){
			e.printStackTrace();
		}
	}
	
	
	public static int UploadFiles( String repid ){
		return UploadFiles( repid, "" );
	}
	public static int UploadFiles( String repid, String title_formula ){
		int r = -1;
		
		try{
			//. 初期設定読み込み
//			readParam( "n2h.ini" );
			
			if( title_formula != null && title_formula.length() > 0 ){
				doc_title_formula = "/document/item[@name=\"" + title_formula + "\"]";
			}
			
			//. Step 3-0 : アップロード先の確認
			
			//. アップロードサーバー
			target = post_service;
			if( !target.startsWith( "http" ) ){
				target = "http://" + target;
			}
			
			r = 0; //. ??
			
			//. K.Kimura (2013/Nov/12)
			//. アップロード順序は以下のようにする：
			//.  (1) システム画像リソース
			//.  (2) ビュー（カテゴリー）
			//.  (3) フォーム内の添付ファイルと画像オブジェクトを逐次変換しながら
			//.  (4) 文書内の添付ファイルと画像オブジェクトを逐次変換しながら
			//.  (5) 各文書の XSL を生成
			//.　  (6) 文書を XSL トランスフォームしながら
			//. これらを unid2wpid['UNID'] = id の連想配列に格納しながらポストする
			//. 最後にDBサーバー側で viewlink, doclink を書き換える
			unid2wpid = new HashMap<String,String>();
			
			//. Step 3-1 : システム画像リソース（ＤＢアイコン含む）	
			//. 貼り付けイメージは最後の方がいい？？
			File picture_dir = new File( repid + "/" + html_folder + "/" + picture_folder );
			if( picture_dir.isDirectory() ){
				File[] picture_files = picture_dir.listFiles();
				for( int i = 0; i < picture_files.length; i ++ ){
					//uploadFile( repid, picture_folder, picture_files[i], username );
					String filename = picture_files[i].getName();
					unid2wpid.put( filename, postFile( repid, picture_files[i], "1" ) );
				}
			}	

			File sys_imgs_dir = new File( repid + "/" + html_folder + "/" + sys_imgs_folder );
			if( sys_imgs_dir.isDirectory() ){
				File[] sys_imgs_files = sys_imgs_dir.listFiles();
				for( int i = 0; i < sys_imgs_files.length; i ++ ){
//					uploadFile( repid, sys_imgs_folder, sys_imgs_files[i], username );
					String filename = sys_imgs_files[i].getName();
					unid2wpid.put( filename, postFile( repid, sys_imgs_files[i], "1" ) );
				}
			}	
			
			File imgresources_dir = new File( repid + "/" + html_folder + "/" + imageresource_folder );
			if( imgresources_dir.isDirectory() ){
				File[] imgresources_files = imgresources_dir.listFiles();
				for( int i = 0; i < imgresources_files.length; i ++ ){
//					uploadFile( repid, imageresource_folder, imgresources_files[i], username );
					String filename = imgresources_files[i].getName();
					unid2wpid.put( filename, postFile( repid, imgresources_files[i], "1" ) );
				}
			}	

			r ++;
			DbgPrintln( "STEP 3-1 done.\n" );

			//. Step 3-2 : ビュー／フォルダのカテゴリを作成
			String viewslist_filename = repid + "/" + html_folder + "/views_list.xml";
			String viewslist_xml = readFileUTF8( viewslist_filename );
//			Element docElement = getRootElement( new File( docfilename ) );
			Element databaseElement = getRootElement( viewslist_xml );
			NodeList viewList = databaseElement.getElementsByTagName( "view" );
			for( int i = 0; i < viewList.getLength(); i ++ ){
				Element viewElement = ( Element )viewList.item( i );
				String view_unid = viewElement.getAttribute( "unid" );
				String view_name = viewElement.getAttribute( "name" );

				unid2wpid.put( view_unid, postCategory( repid, view_unid, view_name ) );
			}

			r ++;
			DbgPrintln( "STEP 3-2 done.\n" );
			
			
			//. unid2pwpid の参照
			Iterator it = unid2wpid.keySet().iterator();
			while( it.hasNext() ){
				Object o = it.next();
				System.out.println( o + " = " + unid2wpid.get( o ) );
			}

			
			//. Step 3-3 : フォーム内の画像や添付ファイル	
			File form_xml_dir = new File( repid + "/" + form_xml_folder );
			if( form_xml_dir.isDirectory() ){
				File[] form_files = form_xml_dir.listFiles();
				for( int i = 0; i < form_files.length; i ++ ){
					String form_filename = form_files[i].getAbsolutePath();
					DbgPrint( " DXL for form #(" + ( i + 1 ) + "/" + form_files.length + ") is updating.. " );
					
					//. この後、フォームデータを直接編集するのでオリジナルを退避
					String form_filename2 = form_filename.replace( form_xml_folder, html_folder );
					form_filename2 = form_filename2.replace( ".xml", ".dxl" );
					
					//. フォームの DXL を取得
					String form_dxl = readFileUTF8( form_filename );
					
					//. DXL を整形
					int n1 = form_dxl.indexOf( "<field " );
					while( n1 > -1 ){
						int n2 = form_dxl.indexOf( ">", n1 + 1 );
						if( n1 < n2 ){
							String s1 = form_dxl.substring( 0, n2 );
							String s2 = form_dxl.substring( n2 );
							form_dxl = s1 + "/" + s2;
						}else{
							n2 = form_dxl.length();
						}
						
						n1 = form_dxl.indexOf( "<field ", n2 + 1 );
					}
//					form_dxl = form_dxl.replaceAll( "(<field[^>]*)>", "$1/>" );
					form_dxl = form_dxl.replaceAll( "</field[^>]*>", "" );
					form_dxl = form_dxl.replaceAll( "<compositedata[^<]*</compositedata>", "" );
					form_dxl = form_dxl.replaceAll( "<compositedata[^>]*>", "" );
					form_dxl = form_dxl.replaceAll( "</compositedata>", "" );

					//. XSL 内のタグを調整
					//. K.Kimura (2013/Nov/18) 自DB内リンクはOK、他DB内リンクはまだ
					form_dxl = swapNotesLinks( repid, form_dxl );

					while( form_dxl.indexOf( "<notesbitmap" ) > -1 ){
						//. ペーストされた画像は切り取る
						form_dxl = omitElement( form_dxl, "notesbitmap" );
					}
					
					form_dxl = form_dxl.replaceAll( "<par ", "<p " );
					form_dxl = form_dxl.replaceAll( "</par>", "</p>" );

					//. 書き戻す
					writeFileUTF8( form_filename2, form_dxl );
					
					DbgPrintln( " done. " );
				}
			}

			r ++;
			DbgPrintln( "STEP 3-3 done.\n" );			

			
			//. Step 3-4 : 文書内の画像や添付ファイル	
			File doc_dir = new File( repid + "/" + doc_folder );
			if( doc_dir.isDirectory() ){
				File[] doc_files = doc_dir.listFiles();
				for( int i = 0; i < doc_files.length; i ++ ){
					String doc_filename = doc_files[i].getAbsolutePath();
					DbgPrint( " XSL for document #(" + ( i + 1 ) + "/" + doc_files.length + ") is searching.. " );
					
					//. この後、文書データを直接編集するのでオリジナルを退避
					String doc_filename2 = doc_filename.replace( doc_folder, html_folder );
					binCopy( doc_filename, doc_filename2 );
					
					//. 再構成
					DbgPrint( " re-organizing.. " );
					String doc_xml = readFileUTF8( doc_filename2 );
					
					//. <?xml?> 宣言文を取り除く
					int n = doc_xml.indexOf( "?>" );
					if( n > -1 ){
						doc_xml = doc_xml.substring( n + 2 );
					}

					//. K.Kimura (2013/Nov/18) 自DB内リンクはOK、他DB内リンクはまだ
					doc_xml = swapNotesLinks( repid, doc_xml );

					while( doc_xml.indexOf( "<notesbitmap" ) > -1 ){
						//. ペーストされた画像は切り取る
						doc_xml = omitElement( doc_xml, "notesbitmap" );
					}
					
					doc_xml = doc_xml.replaceAll( "<par ", "<p class=\"par\" " );
					doc_xml = doc_xml.replaceAll( "</par>", "</p>" );
					writeFileUTF8( doc_filename2, doc_xml );

					int n1 = doc_xml.indexOf( "unid='" );
					int n2 = doc_xml.indexOf( "'", n1 + 6 );
					if( -1 < n1 && n1 < n2 ){
						String unid = doc_xml.substring( n1 + 6, n2 );
						
						//. K.Kimura (2013/Nov/18) 要確認
						omitAttachment( repid, doc_filename2, author_id );
						//. K.Kimura (2013/Nov/18) 要確認
						omitExtra( repid, doc_filename2, unid );
					}
					
					//. セクション
					//. K.Kimura (2013/Nov/18) 要確認
					replaceSection( doc_filename2 );
					
					//. name 属性の item の名前をインターネットアドレスに変換
//					if( pview != null ){
//						replaceName( doc_filename2, pview );
//					}

					DbgPrintln( " done. -> " + doc_filename2 );
				}				
			}

			r ++;
			DbgPrintln( "STEP 3-4 done.\n" );

			
			//. Step 3-5 : 文書毎の XSL 作成
			doc_dir = new File( repid + "/" + html_folder );
			File[] form_files = doc_dir.listFiles();
			for( int j = 0; j < form_files.length; j ++ ){
				String formfilename = form_files[j].getAbsolutePath();
				if( formfilename.endsWith( ".dxl" ) ){
					String form_xsl = readFileUTF8( formfilename );
//					Element docElement = getRootElement( new File( docfilename ) );
					Element formElement = getRootElement( form_xsl );
					
					//. ボディ（<richtext>～</richtext>部分を取り出す）
					String form_body = "";
					int n1 = form_xsl.indexOf( "<richtext" );
					int n2 = form_xsl.lastIndexOf( "</richtext>" );
					if( -1 < n1 && n1 < n2 ){
						form_body = form_xsl.substring( n1, n2 + 11 );

						//. XSL のテンプレート用修正
						n1 = form_body.indexOf( "<field " );
						while( n1 > -1 ){
							n2 = form_body.indexOf( ">", n1 + 1 ); //. 実際は "/>" のはず
							if( n2 > n1 ){
								String field_name = "", field_type = "";
								int n01 = form_body.indexOf( "name='", n1 + 1 );
								if( n01 > -1 ){
									int n02 = form_body.indexOf( "'", n01 + 6 );
									if( n02 > n01 ){
										field_name = form_body.substring( n01 + 6, n02 );
									}
								}
								
								n01 = form_body.indexOf( "type='", n1 + 1 );
								if( n01 > -1 ){
									int n02 = form_body.indexOf( "'", n01 + 6 );
									if( n02 > n01 ){
										field_type = form_body.substring( n01 + 6, n02 );
									}
								}
								
								String xsl_type = ( field_type.equals( "text" ) ) ? "value-of" : "copy-of";

								String s1 = form_body.substring( 0, n1 );
								String s3 = form_body.substring( n2 + 1 );
								String s2 = "<xsl:" + xsl_type + " select='/document/item[@name=\"" + field_name + "\"]'/>";
															
								form_body = s1 + s2 + s3;
								
								n1 = form_body.indexOf( "<field " );
							}
						}
						
						form_xsl = "<xsl:template match='/'>\n"
								+ "<html>\n"
								+ "<head>\n";
						
						if( doc_title_formula == null || doc_title_formula.length() == 0 ){
							doc_title_formula = def_doc_title_formula;
						}
						form_xsl += ( "<title><xsl:value-of select='" + doc_title_formula + "'/></title>\n" );

						form_xsl += "</head>\n"
								+ "<body>";
						
//						form_xsl += ( "<style type='text/css'>\n"
//								+ "p.par{ padding-top: 2px; padding-bottom: 2px }"
//								+ "</style>" );

						form_xsl += form_body;

						form_xsl += "</body>\n"
								+ "</html>\n"
								+ "</xsl:template>\n\n";

						form_xsl = "<?xml version='1.0' ?>\n"
								+ "<xsl:stylesheet version='1.0' xmlns:xsl='http://www.w3.org/1999/XSL/Transform'>\n"
								+ "<xsl:output method='html' encoding='UTF-8'/>\n"
								+ form_xsl
								+ "</xsl:stylesheet>\n";

						//. 保存
						String xslfilename = formfilename.replace( ".dxl", ".xsl" );
						writeFileUTF8( xslfilename, form_xsl );
					}
				}
			}

			DbgPrintln( "STEP 3-5 done.\n" );
			
			
			//. Step 3-6 : XSL トランスフォーム（これは最後に）
//			File doc_dir = new File( repid + "/" + html_folder );
			if( doc_dir.isDirectory() ){
				File[] doc_files = doc_dir.listFiles();
				for( int i = 0; i < doc_files.length; i ++ ){
					String docfilename = doc_files[i].getAbsolutePath();
					if( docfilename.endsWith( ".xml" ) ){
						String doc_xml = readFileUTF8( docfilename );
//						Element docElement = getRootElement( new File( docfilename ) );
						Element docElement = getRootElement( doc_xml );
						String doc_form = docElement.getAttribute( "form" );
						
						form_files = doc_dir.listFiles();
						for( int j = 0; j < form_files.length; j ++ ){
							String formfilename = form_files[j].getAbsolutePath();
							if( formfilename.endsWith( ".dxl" ) ){
								String form_xml = readFileUTF8( formfilename );
//								Element docElement = getRootElement( new File( docfilename ) );
								Element formElement = getRootElement( form_xml );
								String formname = formElement.getAttribute( "name" );
								if( doc_form.equals( formname ) ){
									//. 見つけた！
									String xslfilename = formfilename.replace( ".dxl", ".xsl" );
									try{
										String htmlfilename = docfilename.replace( ".xml", ".htm" );
										StreamSource xmlSrc = new StreamSource( docfilename );
										StreamSource xslSrc = new StreamSource( xslfilename );
										StreamResult htmlDst = new StreamResult( new FileOutputStream( htmlfilename ) );
										
										TransformerFactory factory = TransformerFactory.newInstance();
										Transformer transformer = factory.newTransformer( xslSrc );
										transformer.transform( xmlSrc, htmlDst );
										//. HTML化完了
										
										String doc_unid = "", doc_author = "1", doc_title = "", doc_body = "", doc_parent = "0", doc_category = "", doc_tags = "";
										String doc_html = readFileUTF8( htmlfilename );
										
										int n1 = htmlfilename.lastIndexOf( "\\" );
										int n2 = htmlfilename.lastIndexOf( "." );
										if( n1 > -1 && n2 >= n1 + 1 ){
											doc_unid = htmlfilename.substring( n1 + 1, n2 );
										}
										
										n1 = doc_html.indexOf( "<title>" );
										n2 = doc_html.indexOf( "</title>" );
										if( n1 > -1 && n2 >= n1 + 7 ){
											doc_title = doc_html.substring( n1 + 7, n2 );
										}

										n1 = doc_html.indexOf( "<body>" );
										n2 = doc_html.lastIndexOf( "</body>" );
										if( n1 > -1 && n2 >= n1 + 6 ){
											doc_body = doc_html.substring( n1 + 6, n2 );
											doc_body = "<p>" + doc_body + "</p>";
										}
										
										//. この文書が属するビューを取得
										File csv_dir = new File( repid + "/" + doc_in_view_folder );
										if( csv_dir.isDirectory() ){
											File[] csv_files = csv_dir.listFiles();
											for( int k = 0; k < csv_files.length; k ++ ){
												File csvfile = csv_files[k];
												String csvfilename = csvfile.getAbsolutePath();
												if( csvfilename.indexOf( doc_unid ) > -1 ){
													String wpids = "";
													String csv = readFileUTF8( csvfilename );
													String[] viewunids = csv.split( "," );
													for( int c = 0; c < viewunids.length; c ++ ){
														if( viewunids[c].length() > 0 ){
															String wpid = unid2wpid.get( viewunids[c] );
															if( wpids.length() == 0 ){
																wpids = wpid;
															}else{
																wpids += ( "," + wpid );
															}
														}
													}
													System.out.println( "unids = " + csv );
													System.out.println( "-> wpids = " + wpids );
													
													doc_category = wpids;
												}
											}
										}

										//. アップロード
										String pd = postDocument( repid, doc_unid, doc_author, doc_title, doc_body, doc_parent, doc_category, doc_tags );
										System.out.println( "pd = " + pd );
										
										unid2wpid.put( doc_unid, pd );

									}catch( Exception e ){
										e.printStackTrace();
									}
								}
							}
						}
					}
				}
			}

			DbgPrintln( "STEP 3-6 done.\n" );
			
			DbgPrintln( "UploadFiles finished.\n" );
		}catch( Exception e ){
			e.printStackTrace();
		}
		
		return r;
	}

	public static int postPostProcess( String repid ){
		int r = 1;
		
		try{
			//. notesui ページ、およびダミーページの作成
			String[] pageids = null;

			NameValuePair[] params1 = {
					new NameValuePair( "mode", "createpages" ),
					new NameValuePair( "repid", repid ) };

			HttpClient client1 = new HttpClient();
			PostMethod post1 = new PostMethod( target );
			
			post1.setRequestBody( params1 );
			post1.getParams().setContentCharset( "UTF-8" );
			
			int sc1 = client1.executeMethod( post1 );
			System.out.println( " createPages: sc1 = " + sc1 );
			if( sc1 == HttpStatus.SC_OK ){
				String body1 = post1.getResponseBodyAsString();
				System.out.println( " createPages: body1 = " + body1 );

				pageids = body1.split( "," );
				
				r ++;
			}

			//. page_on_front の修正
			if( pageids.length > 1 ){
				NameValuePair[] params2 = {
						new NameValuePair( "mode", "option" ),
						new NameValuePair( "page_on_front", pageids[0] ),
						new NameValuePair( "page_for_posts", pageids[1] ) };

				HttpClient client2 = new HttpClient();
				PostMethod post2 = new PostMethod( target );

				post2.setRequestBody( params2 );
				post2.getParams().setContentCharset( "UTF-8" );
				
				int sc2 = client2.executeMethod( post2 );
				System.out.println( " updateOption: sc2 = " + sc2 );
				if( sc2 == HttpStatus.SC_OK ){
					String body2 = post2.getResponseBodyAsString();
					System.out.println( " updateOption: body2 = " + body2 );

					r ++;
				}
			}

			
			//. 投稿結果のビューリンク・文書リンクを修正する
/*			params = new ArrayList<NameValuePair>();
			params.add( new NameValuePair( "mode", "fixids" ) );
			Iterator it = unid2wpid.keySet().iterator();
			while( it.hasNext() ){
				Object o = it.next();
				params.add( new NameValuePair( ( String )o, unid2wpid.get( o ) ) );
			}
			
			HttpClient client3 = new HttpClient();
			PostMethod post3 = new PostMethod( target );
			
			post3.setRequestBody( ( NameValuePair[] )params.toArray() );
			post3.getParams().setContentCharset( "UTF-8" );
			
			int sc3 = client3.executeMethod( post3 );
			System.out.println( " fixIds: sc3 = " + sc3 );
			if( sc3 == HttpStatus.SC_OK ){
				String body3 = post3.getResponseBodyAsString();
				System.out.println( " fixIds: body3 = " + body3 );

				r ++;
			}
*/

		}catch( Exception e ){
			e.printStackTrace();
			r = r * -1;
		}
		
		return r;
	}
	
	
	
	
	//. 以下、非公開メソッド
	
	private static String postFile( String repid, File file, String author ){
		String r = "";
		System.out.println( "postFile: repid = " + repid + ", file = " + file.getAbsolutePath() + ", author = " + author );
		System.out.println( " postFile: target = " + target );
		
		try{
			if( !offline ){
				HttpClient client = new HttpClient();
				PostMethod post = new PostMethod( target );
				
				Part[] parts = new Part[]{
						new StringPart( "mode", "attachment" ),
						new StringPart( "repid", repid ),
						new StringPart( "post_author", author ),
						new FilePart( "userfile", file )
				};
				post.setRequestEntity( new MultipartRequestEntity( parts, post.getParams() ) );
				
				//. post.setRequestHeader( "Content-Type", "multipart/form-data" );
				int sc = client.executeMethod( post );
				System.out.println( " postFile: sc = " + sc );
				if( sc == HttpStatus.SC_OK ){
					String body = post.getResponseBodyAsString();
					System.out.println( " postFile: body = " + body );

					r = body;
				}
			}
		}catch( Exception e ){
			e.printStackTrace();
			r = "" + e;
		}
		
		return r;
	}
	
	private static String postCategory( String repid, String unid, String name ){
		String r = "";
		System.out.println( "postCategory: repid = " + repid + ", unid = " + unid + ", name = " + name );
		
		try{
			if( !offline ){
				HttpClient client = new HttpClient();
				PostMethod post = new PostMethod( target );
				
				NameValuePair[] params = {
						new NameValuePair( "mode", "category" ),
						new NameValuePair( "cat_name", name ),
						new NameValuePair( "cat_slug", unid ),
						new NameValuePair( "cat_desc", "" ) };
				post.setRequestBody( params );
				post.getParams().setContentCharset( "UTF-8" );
				
				int sc = client.executeMethod( post );
				System.out.println( " postCategory: sc = " + sc );
				if( sc == HttpStatus.SC_OK ){
					String body = post.getResponseBodyAsString();
					System.out.println( " postCategory: body = " + body );

					r = body;
				}
			}
		}catch( Exception e ){
			e.printStackTrace();
			r = "" + e;
		}
		
		return r;
	}
	
	private static String postDocument( String repid, String unid, String author, String title, String content, String parent, String category, String tags ){
		String r = "";
		System.out.println( "postDocument: repid = " + repid + ", unid = " + unid + ", author = " + author + ", title = " + title + ", #content = " + content.length() + ", category = " + category + ", tags = " + tags );
		
		try{
			if( !offline ){
				HttpClient client = new HttpClient();
				PostMethod post = new PostMethod( target );
				
				NameValuePair[] params = {
						new NameValuePair( "mode", "document" ),
						new NameValuePair( "repid", repid ),
						new NameValuePair( "post_author", author ),
						new NameValuePair( "post_title", title ),
						new NameValuePair( "post_content", content ),
						new NameValuePair( "post_parent", parent ),
						new NameValuePair( "post_category", category ),
						new NameValuePair( "post_tags", tags ) };
				post.setRequestBody( params );
				post.getParams().setContentCharset( "UTF-8" );
				
				int sc = client.executeMethod( post );
				System.out.println( " postDocument: sc = " + sc );
				if( sc == HttpStatus.SC_OK ){
					String body = post.getResponseBodyAsString();
					System.out.println( " postDocument: body = " + body );
					
					r = body;
				}
			}
		}catch( Exception e ){
			e.printStackTrace();
			r = "" + e;
		}
		
		return r;
	}

	private static String postPage( String repid, String unid, String author, String title, String content, String parent ){
		String r = "";
		System.out.println( "postPage: repid = " + repid + ", unid = " + unid + ", author = " + author + ", title = " + title + ", #content = " + content.length() );
		
		try{
			if( !offline ){
				HttpClient client = new HttpClient();
				PostMethod post = new PostMethod( target );
				
				NameValuePair[] params = {
						new NameValuePair( "mode", "page" ),
						new NameValuePair( "repid", repid ),
						new NameValuePair( "post_author", author ),
						new NameValuePair( "post_title", title ),
						new NameValuePair( "post_content", content ),
						new NameValuePair( "post_parent", parent ) };
				post.setRequestBody( params );
				post.getParams().setContentCharset( "UTF-8" );
				
				int sc = client.executeMethod( post );
				System.out.println( " postPage: sc = " + sc );
				if( sc == HttpStatus.SC_OK ){
					String body = post.getResponseBodyAsString();
					System.out.println( " postPage: body = " + body );
					
					r = body;
				}
			}
		}catch( Exception e ){
			e.printStackTrace();
			r = "" + e;
		}
		
		return r;
	}
	
	private static String setFrontPage( String id_page_on_front, String id_page_for_posts ){
		String r = "";
		System.out.println( "setFrontPage: id_page_on_front = " + id_page_on_front + ", id_page_for_post = " + id_page_on_front );
		
		try{
			if( !offline ){
				HttpClient client = new HttpClient();
				PostMethod post = new PostMethod( target );
				
				NameValuePair[] params = {
						new NameValuePair( "mode", "option" ),
						new NameValuePair( "page_on_front", id_page_on_front ),
						new NameValuePair( "page_for_posts", id_page_for_posts ) };
				post.setRequestBody( params );
				post.getParams().setContentCharset( "UTF-8" );
				
				int sc = client.executeMethod( post );
				System.out.println( " setFrontPage: sc = " + sc );
				if( sc == HttpStatus.SC_OK ){
					String body = post.getResponseBodyAsString();
					System.out.println( " setFrontPage: body = " + body );
					
					r = body;
				}
			}
		}catch( Exception e ){
			e.printStackTrace();
			r = "" + e;
		}
		
		return r;
	}

	
	
	private static void readParam( String inifilename ){
		//. ファイルをテキストで読み取る
		try{
			BufferedReader br = new BufferedReader( new FileReader( new File( inifilename ) ) );
			String line = br.readLine();
			while( line != null ){
				if( !line.startsWith( ";" ) ){
					String[] tmp = line.split( "=" );
					if( tmp[0].equalsIgnoreCase( "XML_ENCODE" ) ){
						xml_encode = tmp[1];
					}else if( tmp[0].equalsIgnoreCase( "VIEW_LIST_BGCOLOR" ) ){
						view_list_bgcolor = tmp[1];
					}else if( tmp[0].equalsIgnoreCase( "VIEW_BGCOLOR" ) ){
						view_bgcolor = tmp[1];
					}else if( tmp[0].equalsIgnoreCase( "UPLOAD_SERVER" ) ){
						upload_server = tmp[1];
					}else if( tmp[0].equalsIgnoreCase( "UPLOAD_WAIT" ) ){
						try{
							upload_wait = Integer.parseInt( tmp[1] );
						}catch( Exception e ){
						}
					}else if( tmp[0].equalsIgnoreCase( "MOBILE_CSS" ) ){
						mobile_css = tmp[1];
					}else if( tmp[0].equalsIgnoreCase( "DBG" ) ){
						dbg = tmp[1].equalsIgnoreCase( "true" );
					}else if( tmp[0].equalsIgnoreCase( "HIDDEN_VIEW" ) ){
						hidden_view = tmp[1].equalsIgnoreCase( "true" );
					}else if( tmp[0].equalsIgnoreCase( "STANDARD_XSL" ) ){
						standard_xsl = tmp[1];
					}
				}

				line = br.readLine();
			}
			br.close();
		}catch( Exception e ){
			e.printStackTrace();
		}
	}

	private static String traceElement( Node node, boolean isTop ){
		String r = "";
		
		try{
			int type = node.getNodeType();			
			if( type == Node.ELEMENT_NODE ){
				//. 自分自身
				String tagname = ( ( Element )node ).getTagName();
				r += ( "<" + tagname );

				//. 属性
				NamedNodeMap attrs = node.getAttributes();
				if( attrs != null ){
					for( int i = 0; i < attrs.getLength(); i ++ ){
						Node attr = attrs.item( i );
						String attrName = attr.getNodeName();
						String attrValue = sanitize( attr.getNodeValue() );
						r += ( " " + attrName + "='" + attrValue + "'" );
					}
				}
				
				r += ( ">" );

				//. 子
				if( node.hasChildNodes() ){
					Node child = node.getFirstChild();
					r += traceElement( child, false );
				}
				
				if( type == Node.ELEMENT_NODE ){
					r += ( "</" + tagname + ">" );
				}
			}else if( type == Node.TEXT_NODE ){
				try{
					String text = node.getNodeValue();
					if( text != null ){
						r += sanitize( text );
					}
				}catch( Exception e ){
					e.printStackTrace();
				}
			}			
			
			//. 兄弟
			if( !isTop ){
				Node sib = node.getNextSibling();
				if( sib != null ){
					r += traceElement( sib, false );
				}
			}
		}catch( Exception e ){
			e.printStackTrace();
		}
		
		return r;
	}

	private static String sanitize( String s ){
		String r = s;
		
		try{
			r = r.replaceAll( "&", "&amp;" );
			r = r.replaceAll( "<", "&lt;" );
			r = r.replaceAll( ">", "&gt;" );
			r = r.replaceAll( "\"", "&quot;" );
			r = r.replaceAll( "'", "&#39;" );
			
			if( !r.equals( s ) ){
				s = s + "";
			}
		}catch( Exception e ){
			e.printStackTrace();
		}
		
		return r;
	}
	
	private static String unsanitize( String s ){
		String r = s;
		
		try{
			r = r.replaceAll( "&#39;", "'" );
			r = r.replaceAll( "&quot;", "\"" );
			r = r.replaceAll( "&gt;", ">" );
			r = r.replaceAll( "&lt;", "<" );
			r = r.replaceAll( "&amp;", "&" );
		}catch( Exception e ){
			e.printStackTrace();
		}
		
		return r;
	}
	
	@SuppressWarnings({ "unused", "unchecked" })
	private static void mobilizeFolder( String css, String foldername, HashMap<String, String> viewmap, String dbname ){
		String repid = "";
		
		int n1 = foldername.lastIndexOf( "/" );
		if( n1 > -1 ){
			int n2 = foldername.lastIndexOf( "/", n1 - 1 ); //. 結果が-1 でもこのまま
			
			repid = foldername.substring( n2 + 1, n1 );
		}
		
		//. フォルダ内のコンテンツをまとめて変換
		File file = new File( foldername );
		if( file.isDirectory() ){
			File[] files = file.listFiles();
			for( int i = 0; i < files.length; i ++ ){
				String filename = files[i].getAbsolutePath();
				if( !filename.endsWith( "index.htm" ) || !filename.endsWith( "views_list.htm" ) ){
					mobilizeFile( files[i] );
				}
			}
		}
		
		//. views_list.htm は別途モバイル用のコンテンツを用意(index.htm はそのまま)
		String html = "<html>\n"
			+ "<head>\n"
			+ "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />\n"
			
			+ "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no\"/>\n"
			+ "<meta name=\"apple-mobile-web-app-capable\" content=\"yes\" /> \n"
			+ "<script type=\"text/javascript\">\n"
			+ "<!--\n"
			+ "  window.onload = function(){\n"
			+ "    setTimeout( scrollTo, 100, 0, 1 );\n"
			+ "  }\n"
			+ "// -->\n"
			+ "</script>\n"
			+ "<link href=\"js/dojox/mobile/themes/" + css + "/" + css + ".css\" rel=\"stylesheet\"></link>\n"
			+ "<script type=\"text/javascript\" src=\"js/dojo/dojo.js\" djConfig=\"parseOnLoad: true\"></script>\n"
			+ "<script language=\"JavaScript\" type=\"text/javascript\">\n"
			+ "dojo.require( \"dojox.mobile.parser\" );\n"
			+ "dojo.require( \"dojox.mobile\" );\n"
			+ "dojo.require( \"dojox.mobile.compat\" );\n"
			+ "</script>\n"

			+ "</head>\n<body>\n"
			+ "<div id=\"settings\" dojoType=\"dojox.mobile.View\" selected=\"true\">\n"
			+ "<h1 align=\"middle\" icon=\"/get?id=" + repid + "-" + picture_folder + "/" + sys_imgs_dbicon + "\" dojoType=\"dojox.mobile.Heading\" back=\"Back\" href=\"javascript:history.back();\">" + dbname + "</h1>";


		//. 検索フィールド
		html += ( "<div align=\"right\"><form name=\"mobsearch\" method=\"post\" action=\"/mobsearch\">\n"
				+ "<input type=\"text\" name=\"key\"/>\n"
				+ "<input type=\"hidden\" name=\"repid\" value=\"" + repid + "\"/>\n"
				+ "</form></div><hr/>\n" );

//		html += "<img src=\"" + picture_folder + "/" + sys_imgs_dbicon + "\"/><p/>";
			
		html += "<ul dojoType=\"dojox.mobile.RoundRectList\">";
		for( Iterator it = viewmap.entrySet().iterator(); it.hasNext(); ){
			Map.Entry entry = ( Map.Entry )it.next();
			String view_unid = ( String )entry.getKey();
			String view_name = ( String )entry.getValue();

			html += ( "<li dojoType=\"dojox.mobile.ListItem\" transition=\"slide\" class=\"mblVariableHeight\" style=\"font-size:13px\"><a href=\"" + view_unid + ".htm\" class=\"lnk\">" + view_name + "</a></li>\n" );
		}
		html += ( "</ul>\n" );
	
		html += "</body>\n</html>\n";
	
		String filename = foldername + "/views_list.htm";
		writeFileUTF8( filename, html );
	}

	private static void mobilizeFile( File file ){
		if( file == null || !file.exists() ){
			return;
		}
		
		if( file.isFile() && file.getAbsolutePath().endsWith( ".htm" ) ){
			//. Dojo コンテンツに変換
			try{
				String html = readFileUTF8( file.getAbsolutePath() );
				
				//. target="***" を取り除く
				int n1 = html.indexOf( " target=\"" );
				while( n1 > -1 ){
					int n2 = html.indexOf( "\"", n1 + 9 );
					String s1 = html.substring( 0, n1 );
					String s2 = html.substring( n2 + 1 );
					html = s1 + s2;
					
					n1 = html.indexOf( " target=\"" );
				}
				
				//. -index.htm を -views_list.htm に変更する
				html = html.replaceAll( "-index.htm\"", "-views_list.htm\"" );
				
				//. タグ埋め込み
				html = html.replaceAll( "<ul", "<ul dojoType=\"dojox.mobile.RoundRectList\"" );
				html = html.replaceAll( "<li", "<li dojoType=\"dojox.mobile.ListItem\" transition=\"slide\"" );
				
				//. お約束コードの埋め込み
				String tag = "<head>";
				int n = html.indexOf( tag );
				if( n > -1 ){
					String s1 = html.substring( 0, n + tag.length() );
					String s2 = html.substring( n + tag.length() );
					html = s1 + "\n"
							+ "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no\"/>\n"
							+ "<meta name=\"apple-mobile-web-app-capable\" content=\"yes\" /> \n"
							+ "<script type=\"text/javascript\">\n"
							+ "<!--\n"
							+ "  window.onload = function(){\n"
							+ "    setTimeout( scrollTo, 100, 0, 1 );\n"
							+ "  }\n"
							+ "// -->\n"
							+ "</script>\n"
							+ "<link href=\"js/dojox/mobile/themes/" + mobile_css + "/" + mobile_css + ".css\" rel=\"stylesheet\"></link>\n"
							+ "<script type=\"text/javascript\" src=\"js/dojo/dojo.js\" djConfig=\"parseOnLoad: true\"></script>\n"
							+ "<script language=\"JavaScript\" type=\"text/javascript\">\n"
							+ "dojo.require( \"dojox.mobile.parser\" );\n"
							+ "dojo.require( \"dojox.mobile\" );\n"
							+ "dojo.require( \"dojox.mobile.compat\" );\n"
							+ "</script>\n"
							+ s2;
				}
				
				//. タイトル文字列を取得して画面に反映
				String title = "";
				n1 = html.indexOf( "<title>" );
				int n2 = html.indexOf( "</title>", n1 + 1 );
				if( -1 < n1 && n1 < n2 ){
					title = html.substring( n1 + 7, n2 );
				}

				tag = "<body";
				n1 = html.indexOf( tag );
				if( n1 > -1 ){
					n2 = html.indexOf( ">", n1 + 1 );
					if( n1 < n2 ){
						String s1 = html.substring( 0, n2 + 1 );
						String s2 = html.substring( n2 + 1 );
						html = s1 + "\n"
								+ "<div id=\"settings\" dojoType=\"dojox.mobile.View\" selected=\"true\">\n"
								+ "<h1 dojoType=\"dojox.mobile.Heading\" back=\"Back\" href=\"javascript:history.back();\">" + title + "</h1>\n"
								+ s2;
					}
				}				

				tag = "</body>";
				n = html.indexOf( tag );
				if( n > -1 ){
					String s1 = html.substring( 0, n );
					String s2 = html.substring( n );
					html = s1 + "\n"
							+ "</div>\n"
							+ s2;
				}				

				writeFileUTF8( file.getAbsolutePath(), html );
			}catch( Exception e ){
				e.printStackTrace();
			}
		}		
	}
	
	private static String replaceall( String src, String s1, String s2 ){
		int n = src.indexOf( s1 );
		while( n > -1 ){
			src = src.replace( s1, s2 );
			n = src.indexOf( s1 );
		};
		
		return src;
	}
	
	@SuppressWarnings("unused")
	private static void uploadDXL( String repid, String xmlfilename ){
		String id = "", kind = "", name = "", aliases = "";
		File file = null;
		String target1 = target + "uploaddxl";
		//. domain, repid はグローバル変数として NDJavaLib 内で定義済み
		
		//. id, kind, name, alias, data をファイルから取得する
		try{
///			String dxl = readFileUTF8( xmlfilename );
///			Element rootElement = getRootElement( dxl );
			Element rootElement = getRootElement( new File( xmlfilename ) );

			try{
				id = getUnid( rootElement );
			}catch( Exception e ){
			}
			try{
				kind = rootElement.getTagName();
			}catch( Exception e ){
			}
			try{
				name = rootElement.getAttribute( "name" );
			}catch( Exception e ){
			}
			try{
				aliases = rootElement.getAttribute( "alias" );
			}catch( Exception e ){
			}
			
			try{
				file = new File( xmlfilename );
			}catch( Exception e ){
			}
		}catch( Exception e ){
			e.printStackTrace();
		}
		
		if( id != null && id.length() > 0 && file.exists() && file.isFile() ){
			//. アップロード
			try{
				HttpClient client = new HttpClient();
				PostMethod post = new PostMethod( target1 );
				
				DbgPrintln( "Uploading DXL: repid = " + repid + ", id = " + id + ", domain = " + domain + ", kind = " + kind + ", name = " + name + ", aliases = " + aliases + ", data = (" + xmlfilename + ")" );
				
				Part[] parts = new Part[] {
					new StringPart( "id", id ),
					new StringPart( "repid", repid ),
					new StringPart( "domain", domain ),
					new StringPart( "kind", kind ),
					new StringPart( "name", name ),
					new StringPart( "aliases", aliases ),
					new FilePart( "data", file )
				};
				
				post.setRequestEntity( new MultipartRequestEntity( parts, post.getParams() ) );
				client.executeMethod( post );
				try{
					Thread.sleep( upload_wait );
				}catch( Exception e ){
				}
			}catch( Exception e ){
				e.printStackTrace();
			}
		}else{
			DbgPrintln( "uploadDXL: Id might be missing.. (id = " + id + ")" );
		}

	}
	
	@SuppressWarnings("unused")
	private static int copyFolder( String src_foldername, String dst_foldername ){
		int r = 0;
		
		File file = new File( src_foldername );
		File[] files = file.listFiles();
		for( int i = 0; i < files.length; i ++ ){
			if( files[i].isFile() ){
				//. ファイルをコピー
				copyFile( files[i], src_foldername, dst_foldername );
				r ++;
			}else if( files[i].isDirectory() ){
//				createFolder( files[i].getAbsolutePath() );
				File[] subfiles = files[i].listFiles();
				for( int j = 0; j < subfiles.length; j ++ ){
					//. サブディレクトリ内のファイルをコピー
					copyFile( subfiles[j], src_foldername, dst_foldername );
					r ++;
				}
			}
		}
		
		return r;
	}
	
	private static void copyFile( File src_file, String src_foldername, String dst_foldername ){
		//. src_filename = "C://temp/(repid)/htmls/sub/aaa.htm
		//. src_foldername = "(repid)/htmls
		//. dst_foldername = "(repid)/iphonehtmls
		
		//. 正規化
		String src_filename = replaceall( src_file.getAbsolutePath(), "\\", "/" );
		src_foldername = replaceall( src_foldername, "\\", "/" );
		while( src_foldername.endsWith( "/" ) ){
			src_foldername = src_foldername.substring( 0, src_foldername.length() -1 );
		}
		dst_foldername = replaceall( dst_foldername, "\\", "/" );
		while( dst_foldername.endsWith( "/" ) ){
			dst_foldername = dst_foldername.substring( 0, dst_foldername.length() -1 );
		}

		int n1 = src_filename.lastIndexOf( src_foldername );
		if( n1 > -1 ){
			String filename = src_filename.substring( n1 + src_foldername.length() );
			while( filename.startsWith( "/" ) ){
				//. (/sub)/bbb.jpg
				int n2 = filename.lastIndexOf( "/" );
				if( n2 != 0 ){
					String subfoldername = dst_foldername + filename.substring( 0, n2 );
					createFolder( subfoldername );
				}
				
				filename = filename.substring( 1 ); //. aaa.htm
				String dst_filename = dst_foldername + "/" + filename;
				
				binCopy( src_filename, dst_filename );
			}
		}
	}
	
	private static void uploadAcl( String repid, File file, String by ){
		if( file == null || !file.exists() ){
			return;
		}
		
		if( file.isFile() ){
			//. アップロード
			try{
				HttpClient client = new HttpClient();
				PostMethod post = new PostMethod( target + "uploadacl" );
				
///				String xml = readFileUTF8( file.getAbsolutePath() );
///				Element aclentryElement = getRootElement( xml );
				Element aclentryElement = getRootElement( file );
				String name = aclentryElement.getAttribute( "name" );
				String stype = aclentryElement.getAttribute( "type" );
				if( stype == null ) stype = "";
				String slevel = aclentryElement.getAttribute( "level" );
				String attrnames = "", attrvalues = "";
				
				int type = 0;
				if( stype.equals( "servergroup" ) ){
					type = 5;
				}else if( stype.equals( "person" ) ){
					type = 1;
				}else if( stype.equals( "server" ) ){
					type = 2;
				}else if( stype.equals( "mixedgroup" ) ){
					type = 3;
				}else if( stype.equals( "persongroup" ) ){
					type = 4;
				}
				
				int level = 0;
				if( slevel.equals( "noaccess" ) ){
					level = 0;
				}else if( slevel.equals( "depositor" ) ){
					level = 1;
				}else if( slevel.equals( "reader" ) ){
					level = 2;
				}else if( slevel.equals( "author" ) ){
					level = 3;
				}else if( slevel.equals( "editor" ) ){
					level = 4;
				}else if( slevel.equals( "designer" ) ){
					level = 5;
				}else if( slevel.equals( "manager" ) ){
					level = 6;
				}
				
				NamedNodeMap attrs = aclentryElement.getAttributes();
				if( attrs != null ){
					for( int i = 0; i < attrs.getLength(); i ++ ){
						Node attr = attrs.item( i );
						if( attrnames.length() == 0 ){
							attrnames = attr.getNodeName();
						}else{
							attrnames += ( "," + attr.getNodeName() );
						}

						if( attrvalues.length() == 0 ){
							attrvalues = sanitize( attr.getNodeValue() );
						}else{
							attrvalues += ( "," + sanitize( attr.getNodeValue() ) );
						}
					}
				}

				DbgPrint( "Uploading ACL: repid = " + repid + ", domain = " + domain + ", by = " + by + ", data = (" + file.getAbsolutePath() + ")" );
//				System.out.print( " name = " + name + ", type = " + type + ", level = " + level + ", attrnames = " + attrnames + ", attrvalues = " + attrvalues );
				
				Part[] parts = new Part[] {
					new StringPart( "repid", repid ),
					new StringPart( "name", name ),
					new StringPart( "type", "" + type ),
					new StringPart( "level", "" + level ),
					new StringPart( "attrnames", attrnames ),
					new StringPart( "attrvalues", attrvalues )					
				};
				
				post.setRequestEntity( new MultipartRequestEntity( parts, post.getParams() ) );
				int sc = client.executeMethod( post );
				DbgPrintln( "  -> SC = "  + sc );
				try{
					Thread.sleep( upload_wait );
				}catch( Exception e ){
				}
			}catch( Exception e ){
				e.printStackTrace();
			}
		}		
	}
	
	private static void uploadFile( String repid, String kind, File file, String by ){
		if( file == null || !file.exists() ){
			return;
		}
		
		if( file.isFile() ){
			//. アップロード
			try{
				HttpClient client = new HttpClient();
				PostMethod post = new PostMethod( target + "uploaddxl" );
				
				String id = URLDecoder.decode( file.getAbsolutePath(), "UTF-8" );
				int n = id.lastIndexOf( file_separator + html_folder + file_separator );
				if( n > -1 ){
					id = id.substring( n + ( file_separator + html_folder + file_separator ).length() );
				}else{
					n = id.lastIndexOf( file_separator + mobile_css + html_folder + file_separator );
					id = id.substring( n + ( file_separator + mobile_css + html_folder + file_separator ).length() );
				}
				
				//. ファイル名が日本語の場合に備えて・・ K.Kimura
				id = URLEncoder.encode( id, "UTF-8" );
				id = id.replaceAll( "%2f", "/" );
				id = id.replaceAll( "%2F", "/" );
				id = id.replaceAll( "%5c", "/" );
				id = id.replaceAll( "%5C", "/" );

				DbgPrint( "Uploading File: repid = " + repid + ", id = " + id + ", domain = " + domain + ", kind = " + kind + ", by = " + by + ", data = (" + file.getAbsolutePath() + ")" );
				
				Part[] parts = new Part[] {
					new StringPart( "id", id ),
					new StringPart( "repid", repid ),
					new StringPart( "domain", domain ),
					new StringPart( "kind", kind ),
					new StringPart( "by", by ),
					new FilePart( "data", file )
				};
				
				post.setRequestEntity( new MultipartRequestEntity( parts, post.getParams() ) );
				int sc = client.executeMethod( post );
				DbgPrintln( "  -> SC = "  + sc );
				try{
					Thread.sleep( upload_wait );
				}catch( Exception e ){
				}
			}catch( Exception e ){
				e.printStackTrace();
			}
		}
	}
	
	private static void uploadFile2( String repid, String kind, File file, String by, boolean fx, String xslfilename ){
		if( file == null || !file.exists() ){
			return;
		}
		
		if( file.isFile() ){
			//. アップロード
			try{
				HttpClient client = new HttpClient();
				PostMethod post = new PostMethod( target + "uploaddxl" );
				
				String id = URLDecoder.decode( file.getAbsolutePath(), "UTF-8" );
				int n = id.lastIndexOf( file_separator + html_folder + file_separator );
				if( n > -1 ){
					id = id.substring( n + ( file_separator + html_folder + file_separator ).length() );
				}else{
					n = id.lastIndexOf( file_separator + mobile_css + html_folder + file_separator );
					id = id.substring( n + ( file_separator + mobile_css + html_folder + file_separator ).length() );
				}
				
				//. ファイル名が日本語の場合に備えて・・ K.Kimura
				id = URLEncoder.encode( id, "UTF-8" );
				id = id.replaceAll( "%2f", "/" );
				id = id.replaceAll( "%2F", "/" );
				id = id.replaceAll( "%5c", "/" );
				id = id.replaceAll( "%5C", "/" );

				DbgPrint( "Uploading DXL File: repid = " + repid + ", id = " + id + ", domain = " + domain + ", kind = " + kind + ", by = " + by + ", data = (" + file.getAbsolutePath() + ")" );
				
				Part[] parts = new Part[] {
						new StringPart( "id", id ),
						new StringPart( "repid", repid ),
						new StringPart( "domain", domain ),
						new StringPart( "kind", kind ),
						new StringPart( "by", by ),
						new FilePart( "data", file )
					};
				
				post.setRequestEntity( new MultipartRequestEntity( parts, post.getParams() ) );
				int sc = client.executeMethod( post );
				DbgPrintln( "  -> SC = "  + sc );
				try{
					Thread.sleep( upload_wait );
				}catch( Exception e ){
				}

				if( fx ){
					File file1 = new File( xslfilename );
					HttpClient client1 = new HttpClient();
					PostMethod post1 = new PostMethod( target + "uploadformxsl" );

					DbgPrint( "Uploading FormXsl: repid = " + repid + ", id = " + id + ", datafileid = " + xslfilename + ", data = (" + file1.getAbsolutePath() + ")" );

					parts = new Part[] {
							new StringPart( "id", id ),
							new StringPart( "repid", repid ),
							new StringPart( "datafileid", xslfilename ),
							new FilePart( "data", file1 )
						};

					post1.setRequestEntity( new MultipartRequestEntity( parts, post.getParams() ) );
					sc = client1.executeMethod( post1 );
					DbgPrintln( "  -> SC = "  + sc );
					try{
						Thread.sleep( upload_wait );
					}catch( Exception e ){
					}
				}
			}catch( Exception e ){
				e.printStackTrace();
			}
		}
	}

	private static void uploadStandardXsl( String repid, String xslfilename ){
		File file = new File( xslfilename );
		
		if( file == null || !file.exists() ){
			return;
		}
		
		if( file.isFile() ){
			//. アップロード
			try{
				HttpClient client = new HttpClient();
				PostMethod post = new PostMethod( target + "uploaddatafile" );
				
				String id = xslfilename;
				
				DbgPrint( "Uploading Standard XSL: id = " + id + ", repid = " + repid + ", data = (" + file.getAbsolutePath() + ")" );

				Part[] parts = new Part[] {
						new StringPart( "id", id ),
						new StringPart( "repid", repid ),
						new FilePart( "data", file )
					};
				
				post.setRequestEntity( new MultipartRequestEntity( parts, post.getParams() ) );
				int sc = client.executeMethod( post );
				DbgPrintln( "  -> SC = "  + sc );
				try{
					Thread.sleep( upload_wait );
				}catch( Exception e ){
				}
			}catch( Exception e ){
				e.printStackTrace();
			}
		}
	}
	
	private static void uploadHelpDxl( String repid, String filename, String by ){
///		String help_dxl = readFileUTF8( filename );
		File file = new File( filename );
		
		if( file == null || !file.exists() ){
			return;
		}
		
		if( file.isFile() ){
			//. アップロード
			try{
///				Element formElement = null, helpElement = getRootElement( help_dxl );
				Element formElement = null, helpElement = getRootElement( file );

				//. unid
				String help_unid = getUnid( helpElement );
				
				HttpClient client = new HttpClient();
				PostMethod post = new PostMethod( target + "uploaddxl" );
				
				String id = URLDecoder.decode( file.getAbsolutePath(), "UTF-8" );
				int n = id.lastIndexOf( file_separator + html_folder + file_separator );
				if( n > -1 ){
					id = id.substring( n + ( file_separator + html_folder + file_separator ).length() );
				}else{
					n = id.lastIndexOf( file_separator + mobile_css + html_folder + file_separator );
					id = id.substring( n + ( file_separator + mobile_css + html_folder + file_separator ).length() );
				}

				DbgPrint( "Uploading Help DXL File: repid = " + repid + ", id = " + id + ", domain = " + domain + ", by = " + by + ", data = (" + file.getAbsolutePath() + ")" );

				Part[] parts = new Part[] {
						new StringPart( "id", id ),
						new StringPart( "repid", repid ),
						new StringPart( "domain", domain ),
						new StringPart( "kind", "xml" ),
						new StringPart( "by", by ),
						new FilePart( "data", file )
					};

				post.setRequestEntity( new MultipartRequestEntity( parts, post.getParams() ) );
				int sc = client.executeMethod( post );
				DbgPrintln( "  -> SC = "  + sc );
				try{
					Thread.sleep( upload_wait );
				}catch( Exception e ){
				}
			}catch( Exception e ){
				e.printStackTrace();
			}
		}
	}
	
	private static void uploadHelpXml( String repid, String filename, String by ){
///		String help_xml = readFileUTF8( filename );
		File file = new File( filename );
		
		if( file == null || !file.exists() ){
			return;
		}
		
		if( file.isFile() ){
			//. アップロード
			try{
///				Element formElement = null, helpElement = getRootElement( help_xml );
				Element formElement = null, helpElement = getRootElement( file );

				//. unid
				String help_unid = getUnid( helpElement );
//				String xslid = help_unid;					
				String views = "";
				
				//. この文書に含まれる作成者フィールド
				String readers = "", editors = "";
				NodeList itemList = helpElement.getElementsByTagName( "item" );
				for( int j = 0; j < itemList.getLength(); j ++ ){
					Element itemElement = ( Element )itemList.item( j );

					try{
						String attr_authors = itemElement.getAttribute( "authors" );
						if( attr_authors.equals( "true" ) ){
							//. 作成者フィールド
							NodeList textList = itemElement.getElementsByTagName( "text" );
							for( int k = 0; k < textList.getLength(); k ++ ){
								Element textElement = ( Element )textList.item( k );
								String name = textElement.getFirstChild().getNodeValue();
								
								if( editors.length() == 0 ){
									editors = name;
								}else{
									editors += ( "," + name );
								}
							}
						}
					}catch( Exception e ){
					}
				}
				
				HttpClient client = new HttpClient();
				PostMethod post = new PostMethod( target + "uploaddoc" );
				
				String id = URLDecoder.decode( file.getAbsolutePath(), "UTF-8" );
				int n = id.lastIndexOf( file_separator + html_folder + file_separator );
				if( n > -1 ){
					id = id.substring( n + ( file_separator + html_folder + file_separator ).length() );
				}else{
					n = id.lastIndexOf( file_separator + mobile_css + html_folder + file_separator );
					id = id.substring( n + ( file_separator + mobile_css + html_folder + file_separator ).length() );
				}

				DbgPrint( "Uploading Help XML File: repid = " + repid + ", id = " + id + ", domain = " + domain + ", by = " + by + ", data = (" + file.getAbsolutePath() + ")" );
				
				Part[] parts = new Part[] {
					new StringPart( "id", id ),
					new StringPart( "repid", repid ),
					new StringPart( "domain", domain ),
					new StringPart( "xslid", help_unid ),
					new StringPart( "view", views ),
					new StringPart( "readers", readers ),
					new StringPart( "editors", editors ),
					new StringPart( "by", by ),
					new FilePart( "data", file )
				};
				
				post.setRequestEntity( new MultipartRequestEntity( parts, post.getParams() ) );
				int sc = client.executeMethod( post );
				DbgPrintln( "  -> SC = "  + sc );
				try{
					Thread.sleep( upload_wait );
				}catch( Exception e ){
				}
			}catch( Exception e ){
				e.printStackTrace();
			}
		}
	}
	
	private static void uploadDocFile( String repid, String filename, String by ){
///		String doc_dxl = readFileUTF8( filename );
		File file = new File( filename );
		
		if( file == null || !file.exists() ){
			return;
		}
		
		if( file.isFile() ){
			//. アップロード
			try{
///				Element formElement = null, documentElement = getRootElement( doc_dxl );
				Element formElement = null, documentElement = getRootElement( file );

				//. doc unid
				String xslid = "", doc_unid = getUnid( documentElement );
				String views = "", readers = "", editors = "";

				if( !doc_unid.equals( "(nounid)" ) ){
					String form_name = documentElement.getAttribute( "form" );
					if( form_name != null && form_name.length() > 0 ){
						//. フォームを探す
						File form_dir = new File( repid + "/" + form_folder );
						if( form_dir.isDirectory() ){
							int def_form_idx = -1;
							File[] form_files = form_dir.listFiles();
							for( int j = 0; j < form_files.length && formElement == null; j ++ ){
								String form_dxl = readFileUTF8( form_files[j].getAbsolutePath() );								
								form_dxl = form_dxl.replaceAll( "<br>", "<br/>" );
								Element tmpElement = getRootElement( form_dxl ); // <form>
								
								String tag = tmpElement.getTagName();
								if( tag.equals( "form" ) ){
									if( isDefaultForm( tmpElement ) ){
										def_form_idx = j;
									}
									
									String tmp_name = getElementAttr( tmpElement, "name" );
									
									boolean b = tmp_name.equals( form_name );
									if( !b ){
										try{
											String[] alias_names = getElementAttrs( tmpElement, "alias" );
											for( int k = 0; k < alias_names.length && !b; k ++ ){
												b = alias_names[k].equals( form_name );
											}
										}catch( Exception e ){
										}
									}
									
									if( b ){
										formElement = tmpElement;
									}
								}
							}
							
							if( formElement == null && def_form_idx > -1 ){
								//. 明示的に見つからなかった場合はデフォルトフォームを使う
								String form_dxl = readFileUTF8( form_files[def_form_idx].getAbsolutePath() );								
								form_dxl = form_dxl.replaceAll( "<br>", "<br/>" );
								
								formElement = getRootElement( form_dxl ); // <form>
							}
						}
					}
					
					if( formElement != null ){
						//. フォームが見つかった
						xslid = getUnid( formElement );
					}
					
					//. この文書が表示されるビュー一覧（ダブりは排除済み）
					String csvfilename = repid + "/" + doc_in_view_folder + "/" + doc_unid + ".csv";
					views = readFileUTF8( csvfilename );
					
					//. この文書に含まれる読者フィールド／作成者フィールド
					NodeList itemList = documentElement.getElementsByTagName( "item" );
					for( int j = 0; j < itemList.getLength(); j ++ ){
						Element itemElement = ( Element )itemList.item( j );

						try{
							String attr_readers = itemElement.getAttribute( "readers" );
							if( attr_readers.equals( "true" ) ){
								//. 読者フィールド
								NodeList textList = itemElement.getElementsByTagName( "text" );
								for( int k = 0; k < textList.getLength(); k ++ ){
									Element textElement = ( Element )textList.item( k );
									String name = textElement.getFirstChild().getNodeValue();
									
									if( readers.length() == 0 ){
										readers = name;
									}else{
										readers += ( "," + name );
									}
								}
							}
						}catch( Exception e ){
						}
						
						try{
							String attr_authors = itemElement.getAttribute( "authors" );
							if( attr_authors.equals( "true" ) ){
								//. 作成者フィールド
								NodeList textList = itemElement.getElementsByTagName( "text" );
								for( int k = 0; k < textList.getLength(); k ++ ){
									Element textElement = ( Element )textList.item( k );
									String name = textElement.getFirstChild().getNodeValue();
									
									if( editors.length() == 0 ){
										editors = name;
									}else{
										editors += ( "," + name );
									}
								}
							}
						}catch( Exception e ){
						}
					}
				}
				
				HttpClient client = new HttpClient();
				PostMethod post = new PostMethod( target + "uploaddoc" );
					
				String id = URLDecoder.decode( file.getAbsolutePath(), "UTF-8" );
				int n = id.lastIndexOf( file_separator + html_folder + file_separator );
				if( n > -1 ){
					id = id.substring( n + ( file_separator + html_folder + file_separator ).length() );
				}else{
					n = id.lastIndexOf( file_separator + mobile_css + html_folder + file_separator );
					id = id.substring( n + ( file_separator + mobile_css + html_folder + file_separator ).length() );
				}

				DbgPrint( "Uploading Doc File: repid = " + repid + ", id = " + id + ", domain = " + domain + ", by = " + by + ", data = (" + file.getAbsolutePath() + ")" );
					
				Part[] parts = new Part[] {
					new StringPart( "id", id ),
					new StringPart( "repid", repid ),
					new StringPart( "domain", domain ),
					new StringPart( "xslid", xslid ),
					new StringPart( "view", views ),
					new StringPart( "readers", readers ),
					new StringPart( "editors", editors ),
					new StringPart( "by", by ),
					new FilePart( "data", file )
				};
					
				post.setRequestEntity( new MultipartRequestEntity( parts, post.getParams() ) );
				int sc = client.executeMethod( post );
				DbgPrintln( "  -> SC = "  + sc );
				try{
					Thread.sleep( upload_wait );
				}catch( Exception e ){
				}
			}catch( Exception e ){
				e.printStackTrace();
			}
		}
	}
	
	public static void uploadFile3( String repid, String kind, File file, String by ){
		if( file == null || !file.exists() ){
			return;
		}
		
		if( file.isFile() ){
			//. アップロード
			try{
				HttpClient client = new HttpClient();
				PostMethod post = new PostMethod( target + "uploaddxl" );
				
				String id = URLDecoder.decode( file.getAbsolutePath(), "UTF-8" );
				int n = id.lastIndexOf( file_separator + html_folder + file_separator );
				if( n > -1 ){
					id = id.substring( n + ( file_separator + html_folder + file_separator ).length() );
				}
				
				//. ファイル名が日本語の場合に備えて・・
				id = URLEncoder.encode( id, "UTF-8" );
				id = id.replaceAll( "%2f", "/" );
				id = id.replaceAll( "%2F", "/" );
				id = id.replaceAll( "%5c", "/" );
				id = id.replaceAll( "%5C", "/" );

				System.out.print( "Uploading File: repid = " + repid + ", id = " + id + ", domain = " + domain + ", kind = " + kind + ", by = " + by + ", data = (" + file.getAbsolutePath() + ")" );
				
				Part[] parts = new Part[] {
					new StringPart( "id", id ),
					new StringPart( "repid", repid ),
					new StringPart( "domain", domain ),
					new StringPart( "kind", kind ),
					new StringPart( "by", by ),
					new FilePart( "data", file )
				};
				
				post.setRequestEntity( new MultipartRequestEntity( parts, post.getParams() ) );
				int sc = client.executeMethod( post );
				NDJavaLib.DbgPrintln( "  -> SC = "  + sc );
				try{
					Thread.sleep( upload_wait );
				}catch( Exception e ){
				}
			}catch( Exception e ){
				e.printStackTrace();
			}
		}
	}
	
	public static void uploadDocFile3( String repid, String filename, String by ){
		String doc_dxl = NDJavaLib.readFileUTF8( filename );
		File file = new File( filename );
		
		if( file == null || !file.exists() ){
			return;
		}
		
		if( file.isFile() ){
			//. アップロード
			try{
				Element formElement = null, documentElement = NDJavaLib.getRootElement( doc_dxl );

				//. doc unid
				String xslid = "", doc_unid = NDJavaLib.getUnid( documentElement );
								
				HttpClient client = new HttpClient();
				PostMethod post = new PostMethod( target + "uploaddoc" );
				
				String id = URLDecoder.decode( file.getAbsolutePath(), "UTF-8" );
				int n = id.lastIndexOf( file_separator + html_folder + file_separator );
				if( n > -1 ){
					id = id.substring( n + ( file_separator + html_folder + file_separator ).length() );
				}

				System.out.print( "Uploading Doc File: repid = " + repid + ", id = " + id + ", domain = " + domain + ", by = " + by + ", data = (" + file.getAbsolutePath() + ")" );
				
				Part[] parts = new Part[] {
					new StringPart( "id", id ),
					new StringPart( "repid", repid ),
					new StringPart( "domain", domain ),
					new StringPart( "xslid", "" ),
					new StringPart( "view", "" ),
					new StringPart( "readers", "" ),
					new StringPart( "editors", "" ),
					new StringPart( "by", by ),
					new FilePart( "data", file )
				};
				
				post.setRequestEntity( new MultipartRequestEntity( parts, post.getParams() ) );
				int sc = client.executeMethod( post );
				NDJavaLib.DbgPrintln( "  -> SC = "  + sc );
				try{
					Thread.sleep( upload_wait );
				}catch( Exception e ){
				}
			}catch( Exception e ){
				e.printStackTrace();
			}
		}
	}

	
	private static int removeFolder( String foldername ){
		File file = new File( foldername );
		return removeFile( file );
	}
	
	private static int removeFile( File file ){
		int r = 0;
		
		if( !file.exists() ){
			return r;
		}
		
		if( file.isFile() ){
			file.delete();
			r ++;
		}
		
		if( file.isDirectory() ){
			File[] files = file.listFiles();
			for( int i = 0; i < files.length; i ++ ){
				removeFile( files[i] );
			}
			file.delete(); //. フォルダそのものを残す場合はコメント
			r ++; //. フォルダを消した場合はカウントする
		}
		
		return r;
	}

	private static boolean createFolder( String foldername ){
		boolean b = false;
		
		try{
			File f = new File( foldername );
			if( f.exists() ){
				f.delete();
			}
			
			b = f.mkdir();
		}catch( Exception e ){
			e.printStackTrace();
		}
		
		return b;
	}
	
	@SuppressWarnings("unused")
	private static void writeTextToFile( String filename, String text ){
		try{
			PrintWriter pw = new PrintWriter( new BufferedWriter( new FileWriter( new File( filename ) ) ) );
			pw.println( text );
			pw.close();
		}catch( Exception e ){
			e.printStackTrace();
		}
	}
	
	private static void appendTextToFile( String filename, String text ){
		try{
			PrintWriter pw = new PrintWriter( new BufferedWriter( new FileWriter( new File( filename ), true ) ) );
			pw.print( text );
			pw.close();
		}catch( Exception e ){
			e.printStackTrace();
		}
	}


	private static String getElementAttr( Element element, String attr ){
		String name = "";
		
		try{
			name = element.getAttribute( attr );
		}catch( Exception e ){
			e.printStackTrace();
		}
		
		return name;
	}
	
	private static String[] getElementAttrs( Element element, String attr ){
		String[] attrs = null;
		String name = getElementAttr( element, attr );

		attrs = name.split( "\\|" );
		
		return attrs;
	}

	private static boolean isDefaultForm( Element formElement ){
		boolean b = false;
		String tmp = "";
		
		try{
			try{
				tmp = formElement.getAttribute( "default" );
			}catch( Exception e ){
			}
			
			b = tmp.equals( "true" );
		}catch( Exception e ){
			e.printStackTrace();
		}
		
		return b;
	}
	
	private static int helpToXMLDXL( String filename, String[] designers, String repid ){
		int r = 0;
		String xml = "", authors = "";
		
		filename = filename.replaceAll( "\\\\", "/" );

		//. 編集権限を持つユーザー／グループのリストを作成
		if( designers == null || designers.length == 0 ){
			authors = "";
		}else if( designers.length == 1 ){
			authors = "<text>" + designers[0] + "</text>";
		}else{
			authors = "<textlist>";
			for( int i = 0; i < designers.length; i ++ ){
				authors += ( "<text>" + designers[i] + "</text>" );
			}
			authors += "</textlist>";
		}
		
//		DbgPrintln( "authors = " + authors );

		//. XML データを取得
		String help_xml = readFileUTF8( filename );

		//. この後、データを編集した後の保存ファイル名
		String filename1 = filename;
		int n0 = filename.lastIndexOf( "/help" );
		if( n0 == -1 ) n0 = filename.lastIndexOf( "\\help" );
		if( n0 > -1 ){
			String s1 = filename.substring( 0, n0 );
			String s2 = filename.substring( n0 );
			filename1 = s1 + "/" + html_folder + s2;
		}

		try{
			Element helpElement = getRootElement( help_xml );
			String unid = getUnid( helpElement );

			//. helpusingdocument か helpaboutdocument かを調べておく
			String tagname = helpElement.getTagName();
			boolean isHelpUsing = tagname.equals( "helpusingdocument" );
			String title = ( isHelpUsing ) ? /*rb.getString( "help.using" ) : rb.getString( "help.about" )*/"データベースの使い方" : "データベースについて";

			//. bgcolor 属性を取り出しておく
			String bgcolor = helpElement.getAttribute( "bgcolor" );
			if( bgcolor == null ) bgcolor = "";
			if( bgcolor.length() == 0 ) bgcolor = "#ffffff";

			//. <helpXXXdocument> → <document>
			int n1 = help_xml.indexOf( "<noteinfo " );
			if( n1 > 0 ){
//				int n2 = help_xml.lastIndexOf( "</help", n1 + 1 );
				int n2 = help_xml.lastIndexOf( "</help" );
				if( n2 > n1 ){
					xml = "<document>\n"
							+ help_xml.substring( n1, n2 )
							+ "\n</document>\n";
					
					int n3 = xml.indexOf( "<body>" );
					if( n3 > 0 ){
						String s1 = xml.substring( 0, n3 );
						String s2 = xml.substring( n3 );
						xml = s1
								+ "<item name='subject'><text>" + title + "</text></item>\n"
								+ "<item name='DoEM_" + unid + "_Es' authors='true' names='true'>" + authors + "</item>\n"
								+ s2;
					}
					
					//. <body> 内をノーツ用に書き換え
					xml = swapNotesLinks( repid, xml );
					while( xml.indexOf( "<notesbitmap" ) > -1 ){
						//. ペーストされた画像は切り取る
						xml = omitElement( xml, "notesbitmap" );
					}
					xml = xml.replaceAll( "<par ", "<p class=\"par\" " );
					xml = xml.replaceAll( "</par>", "</p>" );

					//. <item>を用意
					xml = xml.replace( "<body>", "<item name='body'>" );
					xml = xml.replace( "</body>", "</item>" );
				}
			}

			//. 書き戻す
			writeFileUTF8( filename1, xml );

			//. 添付画像を取り出す、などなど
			omitAttachment( repid, filename1, author_id );
			omitExtra( repid, filename1, unid );

			
			//. DXL 作成
			String filename2 = filename1.replace( ".xml", ".dxl" );
			String help_dxl = readFileUTF8( "help.dxl" );
			
			//. 整形
			help_dxl = help_dxl.replace( "###doem_v_bgcolor###", bgcolor );
			help_dxl = help_dxl.replace( "###doem_v_unid###", unid );
			
			//. 書き戻す
			writeFileUTF8( filename2, help_dxl );
		}catch( Exception e ){
			r = -1;
			e.printStackTrace();
		}
		
		return r;
	}
	
	private static String swapNotesLinks( String repid, String src ){
		try{
			//. DB Link
			String imgfile = unid2wpid.get( sys_imgs_db ); //.sys_imgs_folder + "/" + sys_imgs_db;
			String imgtag = "<img src='" + imgfile + "'/>";
			int n1 = src.indexOf( "<databaselink " );
			while( n1 > -1 ){
				int n2 = src.indexOf( "</databaselink>", n1 + 1 );
				if( n2 > n1 ){
					String dbid = "", desc = "(no desc)", newlink = "";
					int n3 = src.indexOf( "database='", n1 );
					int n4 = src.indexOf( "'", n3 + 10 );
					if( -1 < n3 && n3 < n4 ){
						dbid = src.substring( n3 + 10, n4 );

						n3 = src.indexOf( "description='", n1 );
						n4 = src.indexOf( "'", n3 + 13 );
						if( -1 < n3 && n3 < n4 ){
							desc = src.substring( n3 + 13, n4 );
						}
						
						if( dbid.equals( repid ) ){
							//. 自分自身へのDBリンク
							newlink = " <a href='./' title='" + desc + "'>" + imgtag + "</a>";
						}else{
							//. 他DBへのDBリンク
							newlink = " <a href='../../" + dbid + "/" + html_folder + "/index.xml' title='" + desc + "'>" + imgtag + "</a>";
//							newlink = " <a href='/noteslinkforward?repid=" + dbid + "&type=db' title='" + desc + "'>" + imgtag + "</a>";
						}
					}
					
					String s1 = src.substring( 0, n1 );
					String s2 = src.substring( n2 + 15 );
					src = s1 + newlink + s2;
				}
				
				n1 = src.indexOf( "<databaselink " );
			}
			
			//. View Link
			imgfile = unid2wpid.get( sys_imgs_view ); //.sys_imgs_folder + "/" + sys_imgs_view;
			imgtag = "<img src='" + imgfile + "'/>";
			n1 = src.indexOf( "<viewlink " );
			while( n1 > -1 ){
				int n2 = src.indexOf( "</viewlink>", n1 + 1 );
				if( n2 > n1 ){
					String dbid = "", viewid = "", desc = "(no desc)", newlink = "";
					int n3 = src.indexOf( "database='", n1 );
					int n4 = src.indexOf( "'", n3 + 10 );
					if( -1 < n3 && n3 < n4 ){
						dbid = src.substring( n3 + 10, n4 );

						n3 = src.indexOf( "view='", n1 );
						n4 = src.indexOf( "'", n3 + 6 );
						if( -1 < n3 && n3 < n4 ){
							viewid = src.substring( n3 + 6, n4 );
							
							n3 = src.indexOf( "description='", n1 );
							n4 = src.indexOf( "'", n3 + 13 );
							if( -1 < n3 && n3 < n4 ){
								desc = src.substring( n3 + 13, n4 );
							}
							
							if( dbid.equals( repid ) ){
								//. 自分自身のDBへのビューリンク（必ずクラウドに存在している）
								newlink = " <a href='./?nsf2wpid_viewid=" + viewid + "' title='" + desc + "'>" + imgtag + "</a>";
							}else{
								//. 他DBへのビューリンク
								newlink = " <a href='../../" + dbid + "/" + html_folder + "/" + viewid + ".xml' title='" + desc + "'>" + imgtag + "</a>";
//								newlink = " <a href='/noteslinkforward?repid=" + dbid + "&viewid=" + viewid + "&type=view' title='" + desc + "'>" + imgtag + "</a>";
							}
						}
					}
					
					String s1 = src.substring( 0, n1 );
					String s2 = src.substring( n2 + 11 );
					src = s1 + newlink + s2;
				}
				
				n1 = src.indexOf( "<viewlink " );
			}
			
			//. Document Link
			imgfile = unid2wpid.get( sys_imgs_doc ); //.sys_imgs_folder + "/" + sys_imgs_doc;
			imgtag = "<img src='" + imgfile + "'/>";
			n1 = src.indexOf( "<doclink " );
			while( n1 > -1 ){
				int n2 = src.indexOf( "</doclink>", n1 + 1 );
				if( n2 > n1 ){
					String dbid = "", docid = "", desc = "(no desc)", newlink = "";
					int n3 = src.indexOf( "database='", n1 );
					int n4 = src.indexOf( "'", n3 + 10 );
					if( -1 < n3 && n3 < n4 ){
						dbid = src.substring( n3 + 10, n4 );

						n3 = src.indexOf( "document='", n1 );
						n4 = src.indexOf( "'", n3 + 10 );
						if( -1 < n3 && n3 < n4 ){
							docid = src.substring( n3 + 10, n4 );
							
							n3 = src.indexOf( "description='", n1 );
							n4 = src.indexOf( "'", n3 + 13 );
							if( -1 < n3 && n3 < n4 ){
								desc = src.substring( n3 + 13, n4 );
							}
							
							if( dbid.equals( repid ) ){
								//. 自分自身のDBへの文書リンク（必ずクラウドに存在している）
								newlink = " <a href='./?nsf2wpid_docid=" + docid + "' title='" + desc + "'>" + imgtag + "</a>";
							}else{
								//. 他DBへの文書リンク
								newlink = " <a href='../../" + dbid + "/" + html_folder + "/" + docid + ".xml' title='" + desc + "'>" + imgtag + "</a>";
//								newlink = " <a target='preview' href='/noteslinkforward?repid=" + dbid + "&docid=" + docid + "&type=doc' title='" + desc + "'>" + imgtag + "</a>";
							}
						}
					}
					
					String s1 = src.substring( 0, n1 );
					String s2 = src.substring( n2 + 10 );
					src = s1 + newlink + s2;
				}
				
				n1 = src.indexOf( "<doclink " );
			}
			
			//. URL Link
			src = src.replaceAll( "<urlink ", "<a " );
			src = src.replaceAll( "</urlink>", "</a>" );
		}catch( Exception e ){
			e.printStackTrace();
		}
		
		return src;
	}
	
	private static String swapElement( String src, String tag1, String tag2 ){
		//. <tag1>..<tag2>..</tag2>..</tag1> を	
		//. <tag2>..<tag1>..</tag1>..</tag2> に入れ替える
		
		int n1 = 0;
		while( n1 != -1 ){
			//. まず<tag1>の直後に<tag2>が来ていることを確認する（そのパターンだけが対象）
			n1 = src.indexOf( "<" + tag1, n1 + 1 ); //. tag1 開始
			if( n1 > -1 ){
				int n2 = src.indexOf( ">", n1 + 1 ); //. tag1 終わり

				//. tag1 がそのまま終了していないことを確認
				int n2t = src.indexOf( "/>", n1 + 1 );
				if( -1 == n2t || ( n2t + 1 ) != n2 ){
					int n0 = src.indexOf( "<", n2 + 1 ); //. tag1 の直後の開始タグの位置
					int n3 = src.indexOf( "<" + tag2, n2 + 1 ); //. tag1 の直後の tag2 開始の位置
					if( n0 > -1 && n0 == n3 ){ //. この２つが一致している？
						int n4 = src.indexOf( ">", n3 + 1 );
						
						//. tag2 がそのまま終了していないことを確認
						int n4t = src.indexOf( "/>", n3 + 1 );
						if( -1 == n4t || ( n4t + 1 ) != n4 ){
							//. **(n1)<tag1>(n2)**(n3)<tag2>(n4)**(n5)</tag2>(n6)**(n7)</tag1>(n8)** のパターン
							int n5 = src.indexOf( "</" + tag2, n4 + 1 );
							int n6 = src.indexOf( ">", n5 + 1 );
							int n7 = src.indexOf( "</" + tag1, n6 + 1 );
							int n8 = src.indexOf( ">", n7 + 1 );
							if( n1 < n2 && n2 < n3 && n3 < n4 && n4 < n5 && n5 < n6 && n6 < n7 && n7 < n8 ){
								String s1 = src.substring( 0, n1 );
								String s2 = src.substring( n1, n2 + 1 ); //. <tag1>
								String s3 = src.substring( n2 + 1, n3 );
								String s4 = src.substring( n3, n4 + 1 ); //. <tag2>
								String s5 = src.substring( n4 + 1, n5 );
								String s6 = src.substring( n5, n6 + 1 ); //. </tag2>
								String s7 = src.substring( n6 + 1, n7 );
								String s8 = src.substring( n7, n8 + 1 ); //. </tag1>
								String s9 = src.substring( n8 + 1 );
								
								src = s1 + s4 + s3 + s2 + s5 + s8 + s7 + s6 + s9;
								n1 = n8;
							}else{
								//. いずれかのタグが終了していない？　ありえない
								DbgPrintln( "Tag sequence was wrong(1)." );
							}
						}else{
							//. **(n1)<tag1>(n2)**(n3)<tag2/>(n4)**(n5)</tag1>(n6)** のパターン
							int n5 = src.indexOf( "</" + tag1, n4 + 1 );
							int n6 = src.indexOf( ">", n5 + 1 );
							if( n1 < n2 && n2 < n3 && n3 < n4 && n4 < n5 && n5 < n6 ){
								String s1 = src.substring( 0, n1 );
								String s2 = src.substring( n1, n2 + 1 ); //. <tag1>
								String s3 = src.substring( n2 + 1, n3 );
								String s4 = src.substring( n3, n4 + 1 ); //. <tag2/>
								String s5 = src.substring( n4 + 1, n5 );
								String s6 = src.substring( n5, n6 + 1 ); //. </tag1>
								String s7 = src.substring( n6 );
								
								src = s1 + ( "<" + tag2 + ">" ) + s3 + ( "<" + tag1 + "/>" ) + s5 + ( "</" + tag2 + ">" ) + s7;
								n1 = n6;
							}else{
								//. いずれかのタグが終了していない？　ありえない
								DbgPrintln( "Tag sequence was wrong(2)." );
							}
						}
					}else{
						//. tag1 の直後のタグが tag2 ではなかった場合の処理
						//. 放っておいてもいいが、それだとループ内でこのメソッドは使えない。。
						
						//. 少し意味が違ってしまうが、<tag1> だけを削って <tag2> を残す前提で以下を記述
						//. **(n1)<tag1>(n2)****(n4)</tag1>(n5)++
						int n4 = src.indexOf( "</" + tag1, n2 + 1 );
						int n5 = src.indexOf( ">", n4 + 1 );
						if( n1 < n2 && n2 < n4 && n4 < n5 ){
							String s1 = src.substring( 0, n1 );
//							String s2 = src.substring( n1, n2 + 1 ); //. <tag1>
							String s3 = src.substring( n2 + 1, n4 );
//							String s4 = src.substring( n4, n5 + 1 ); //. </tag1>
							String s5 = src.substring( n5 + 1 );
							
							src = s1 + s3 + s5;
							n1 = n5;
						}else{
							//. いずれかのタグが終了していない？　ありえない
						}
					}
				}
			}
		}
		
		return src;
	}
	
	private static String swapElement2( String src, String tag1, String tag2 ){
		//. <tag1></tag1><tag2>..</tag2> を	
		//. <tag1><tag2>..</tag2></tag1> に入れ替える
		
		int n1 = 0;
		while( n1 != -1 ){
			//. まず<tag1>の直後に<tag2>が来ていることを確認する（そのパターンだけが対象）
			n1 = src.indexOf( "<" + tag1, n1 + 1 ); //. tag1 開始
			if( n1 > -1 ){
				int n2 = src.indexOf( ">", n1 + 1 ); //. tag1 終わり
				int n3 = src.indexOf( "</" + tag1, n2 + 1 ); //. 終了tag1 開始
				int n4 = src.indexOf( ">", n3 + 1 ); //. 終了tag1 終わり
				int n5 = src.indexOf( "<" + tag2, n4 + 1 ); //. 終了tag1 の直後の tag2 開始の位置
				int n0 = src.indexOf( "<", n4 + 1 ); //. 終了tag1 の直後の開始タグの位置
				if( n1 < n2 && n2 < n3 && n3 < n4 && n4 < n5 && n0 == n5 ){ //. この２つが一致している？
					//. --(n1)<tag1>(n2)--(n3)</tag1>(n4)--(n5)<tag2>(n6)**(n7)</tag2>(n8)--
					int n6 = src.indexOf( ">", n5 + 1 ); //. tag2 終わり
					int n7 = src.indexOf( "</" + tag2, n6 + 1 ); //. 終了tag2開始
					int n8 = src.indexOf( ">", n7 + 1 ); //. 終了tag2終わり
					if( n5 < n6 && n6 < n7 && n7 < n8 ){
						String s1 = src.substring( 0, n1 );
						String s2 = src.substring( n1, n2 + 1 ); //. <tag1>
						String s3 = src.substring( n2 + 1, n3 );
						String s4 = src.substring( n3, n4 + 1 ); //. </tag1>
						String s5 = src.substring( n4 + 1, n5 );
						String s6 = src.substring( n5, n6 + 1 ); //. <tag2>
						String s7 = src.substring( n6 + 1, n7 ); //. **
						String s8 = src.substring( n7, n8 + 1 ); //. </tag2>
						String s9 = src.substring( n8 + 1 );
						
						//. **1 <tag1> **2 </tag1> **3 <tag2> **4 </tag2> **5
						//. -> **1 <tag1> **2 **3 <tag2> **4 </tag2> </tag1> **5
						src = s1 + s2 + s3 + s5 + s6 + s7 + s8 + s4 + s9;
						n1 = n8;
					}else{
						//. いずれかのタグが終了していない？　ありえない
					}
				}else{
					//. tag1 の直後のタグが tag2 ではなかった場合の処理
					//. 放っておいてもいいが、それだとループ内でこのメソッドは使えない。。
					n1 ++;
				}
			}
		}
		
		return src;
	}

	private static String omitElement( String src, String tagname ){
		//. tagname : "<....>"　の .... 部分
		try{
			int n1 = src.indexOf( "<" + tagname );
			if( n1 > -1 ){
				int n2 = src.indexOf( ">", n1 + 1 );
				int n0 = src.indexOf( "/", n1 + 1 );
				if( -1 < n0 && n0 < n2 ){
					//. <.../> という形式 -> この部分だけ取り除けばよい
					String s1 = src.substring( 0, n1 );
					String s2 = src.substring( n2 + 1 );
					src = s1 + s2;
				}else{
					//. <...>***</...> という形式 -> タグを含めて中身を消す
					n0 = src.indexOf( "</" + tagname );
					int n3 = src.indexOf( ">", n0 + 1 );
					String s1 = src.substring( 0, n1 );
					String s2 = src.substring( n3 + 1 );
					src = s1 + s2;
				}
			}
		}catch( Exception e ){
			e.printStackTrace();
		}
		
		return src;
	}
	
	private static int replaceSharedElementForm( String repid, String form_filename ){
		int r = 0;
		
		try{
			String form_xml = readFileUTF8( form_filename );
			
			//. <subformref> を該当する Subform の <body><richtext> 内部でリプレースする
			int n = form_xml.indexOf( "<subformref " );
			while( n > -1 ){
				int n1 = n;
				int n2 = form_xml.indexOf( "name='", n1 + 1 );
				int n3 = form_xml.indexOf( ">", n1 + 1 );
				if( n1 < n2 && n2 < n3 ){
					int n4 = form_xml.indexOf( "'", n2 + 6 );
					String subform_name = form_xml.substring( n2 + 6, n4 );
					
					//. 該当する Subform を探す
					File subform_dir = new File( repid + "/" + subform_folder );
					if( subform_dir.isDirectory() ){
						boolean b = true;
						File[] subform_files = subform_dir.listFiles();
						for( int i = 0; i < subform_files.length && b; i ++ ){
							BufferedReader subform_br = new BufferedReader( new FileReader( subform_files[i] ) );
							String subform_dxl = "", subform_line = subform_br.readLine();
							while( subform_line != null ){
								subform_dxl += ( subform_line + "\n" );
								subform_line = subform_br.readLine();
							}
							subform_br.close();

							Element subformElement = getRootElement( subform_dxl );
							String subform_name1 = getElementAttr( subformElement, "name" );
							if( subform_name.equals( subform_name1 ) ){
								//. Subform が見つかった
								NodeList bodyList = subformElement.getElementsByTagName( "body" );
								Element bodyElement = ( Element )bodyList.item( 0 );
								NodeList richtextList = bodyElement.getElementsByTagName( "richtext" );
								Element richtextElement = ( Element )richtextList.item( 0 );
								String richtextValue = traceElement( richtextElement, true );
								
								n3 = form_xml.indexOf( ">", n3 + 1 ); //. </subformref>
								String s1 = form_xml.substring( 0, n1 );
								String s2 = form_xml.substring( n3 + 1 );
								form_xml = s1 + richtextValue + s2;
								
								r ++;
								b = false;
							}
						}
					}
				}
				
				n = form_xml.indexOf( "<subformref " );
			}

			//. <sharedfieldref> を該当する SharedField の <field> 内部でリプレースする
			n = form_xml.indexOf( "<sharedfieldref " );
			while( n > -1 ){
				int n1 = n;
				int n2 = form_xml.indexOf( "name='", n1 + 1 );
				int n3 = form_xml.indexOf( ">", n1 + 1 );
				if( n1 < n2 && n2 < n3 ){
					int n4 = form_xml.indexOf( "'", n2 + 6 );
					String sharedfield_name = form_xml.substring( n2 + 6, n4 );
					
					//. 該当する Sharedfield を探す
					File sharedfield_dir = new File( repid + "/" + sharedfield_folder );
					if( sharedfield_dir.isDirectory() ){
						boolean b = true;
						File[] sharedfield_files = sharedfield_dir.listFiles();
						for( int i = 0; i < sharedfield_files.length && b; i ++ ){
							BufferedReader sharedfield_br = new BufferedReader( new FileReader( sharedfield_files[i] ) );
							String sharedfield_dxl = "", sharedfield_line = sharedfield_br.readLine();
							while( sharedfield_line != null ){
								sharedfield_dxl += ( sharedfield_line + "\n" );
								sharedfield_line = sharedfield_br.readLine();
							}
							sharedfield_br.close();

							Element sharedfieldElement = getRootElement( sharedfield_dxl ); // <sharedfield>

							String sharedfield_name1 = getElementAttr( sharedfieldElement, "name" );
							if( sharedfield_name.equals( sharedfield_name1 ) ){
								//. Sharedfield が見つかった
								NodeList fieldList = sharedfieldElement.getElementsByTagName( "field" );
								Element fieldElement = ( Element )fieldList.item( 0 );
								String fieldValue = traceElement( fieldElement, true );
								
								n3 = form_xml.indexOf( ">", n3 + 1 ); //. </sharedfieldref>
								String s1 = form_xml.substring( 0, n1 );
								String s2 = form_xml.substring( n3 + 1 );
								form_xml = s1 + fieldValue + s2;
								
								r ++;
								b = false;
							}
						}
					}
				}
				
				n = form_xml.indexOf( "<sharedfieldref " );
			}
			
			//. フィールド定義 <field> 内は種類に関係なく空にする（式を残しておいても無駄なので）
			n = form_xml.indexOf( "<field " );
			while( n > -1 ){
				int n1 = n;
				int n2 = form_xml.indexOf( ">", n1 + 1 ); //. <field ..> の終わり
				int n3 = form_xml.indexOf( "</field>", n2 + 1 ); //. <field>の終了タグ
				if( n1 < n2 && n2 < n3 ){
					String s1 = form_xml.substring( 0, n2 + 1 );
					String s2 = form_xml.substring( n3 );
					form_xml = s1 + s2;
				}
				
				n = form_xml.indexOf( "<field ", n + 1 );
			}
			
			//. テキストをファイルに書き戻す
			writeFileUTF8( form_filename, form_xml );
		}catch( Exception e ){
			e.printStackTrace();
		}
		
		return r;
	}
	
	private static int replaceSharedElementView( String repid, String view_filename ){
		int r = 0;
		
		try{
			String view_xml = readFileUTF8( view_filename );

			//. <sharedcolumnref> を該当する SharedColumn の <field> 内部でリプレースする
			int n = view_xml.indexOf( "<sharedcolumnref " );
			while( n > -1 ){
				int n1 = n;
				int n2 = view_xml.indexOf( "name='", n1 + 1 );
				int n3 = view_xml.indexOf( ">", n1 + 1 );
				if( n1 < n2 && n2 < n3 ){
					int n4 = view_xml.indexOf( "'", n2 + 6 );
					String sharedcolumn_name = view_xml.substring( n2 + 6, n4 );
					
					//. 該当する SharedColumn を探す
					File sharedcolumn_dir = new File( repid + "/" + sharedcolumn_folder );
					if( sharedcolumn_dir.isDirectory() ){
						boolean b = true;
						File[] sharedcolumn_files = sharedcolumn_dir.listFiles();
						for( int i = 0; i < sharedcolumn_files.length && b; i ++ ){
							BufferedReader sharedcolumn_br = new BufferedReader( new FileReader( sharedcolumn_files[i] ) );
							String sharedcolumn_dxl = "", sharedcolumn_line = sharedcolumn_br.readLine();
							while( sharedcolumn_line != null ){
								sharedcolumn_dxl += ( sharedcolumn_line + "\n" );
								sharedcolumn_line = sharedcolumn_br.readLine();
							}
							sharedcolumn_br.close();

							Element sharedcolumnElement = getRootElement( sharedcolumn_dxl ); // <sharedfield>

							String sharedcolumn_name1 = getElementAttr( sharedcolumnElement, "name" );
							if( sharedcolumn_name.equals( sharedcolumn_name1 ) ){
								//. SharedColumn が見つかった
								NodeList columnList = sharedcolumnElement.getElementsByTagName( "column" );
								Element columnElement = ( Element )columnList.item( 0 );
								String columnValue = traceElement( columnElement, true );

								n3 = view_xml.indexOf( "</sharedcolumnref>", n3 + 1 ); //. </sharedcolumnref>
								String s1 = view_xml.substring( 0, n1 );
								String s2 = view_xml.substring( n3 + 18 );
								view_xml = s1 + columnValue + s2;
								
								r ++;
								b = false;
							}
						}
					}
				}
				
				n = view_xml.indexOf( "<sharedcolumnref " );
			}
/*
			//. 列定義 <column> 内は種類に関係なく空にする（式を残しておいても無駄なので）
			n = view_xml.indexOf( "<column " );
			while( n > -1 ){
				int n1 = n;
				int n2 = view_xml.indexOf( ">", n1 + 1 ); //. <column ..> の終わり
				int n3 = view_xml.indexOf( "</column>", n2 + 1 ); //. <column>の終了タグ
				if( n1 < n2 && n2 < n3 ){
					String s1 = view_xml.substring( 0, n2 + 1 );
					String s2 = view_xml.substring( n3 );
					view_xml = s1 + s2;
				}
				
				n = view_xml.indexOf( "<column ", n + 1 );
			}
*/
			//. テキストをファイルに書き戻す
			writeFileUTF8( view_filename, view_xml );
			
			//. このままアップロード？
		}catch( Exception e ){
			e.printStackTrace();
		}
		
		return r;
	}

	public static String readFileUTF8( String filename ){
		String lines = "";
		
		try{
			//. ファイルをテキストで読み取る
			try{
				BufferedReader br = new BufferedReader( new InputStreamReader( new FileInputStream( filename ), "UTF-8" ) );
				String line = br.readLine();
				while( line != null ){
					lines += ( line + "\n" );
					line = br.readLine();
				}
				br.close();
			}catch( Exception e ){
				e.printStackTrace();
			}
		}catch( Exception e ){
			e.printStackTrace();
		}
		
		return lines;
	}

	public static int writeFileUTF8( String filename, String lines ){
		int r = 0;
		
		try{
			BufferedWriter bw = new BufferedWriter( new OutputStreamWriter( new FileOutputStream( filename ), "UTF-8" ) );
			bw.write( lines );
			bw.flush();
			bw.close();
		}catch( Exception e ){
			e.printStackTrace();
			r = 1;
		}
		
		return r;
	}

	@SuppressWarnings("unused")
	private static int changeDocfile( String doc_filename ){
		int r = 0;
		
		try{
			//. ファイルをテキストで読み取る
			String doc_xml = readFileUTF8( doc_filename );

			//. リッチテキスト内の表
			doc_xml = doc_xml.replaceAll( "<tablerow", "<tr" );
			doc_xml = doc_xml.replaceAll( "<tablecell", "<td" );
			doc_xml = doc_xml.replaceAll( "</tablerow", "</tr" );
			doc_xml = doc_xml.replaceAll( "</tablecell", "</td" );
			
			//. リッチテキスト内のリンク
			doc_xml = doc_xml.replaceAll( "<urllink", "<a" );
			doc_xml = doc_xml.replaceAll( "</urllink>", "</a>" );
			
			//. テキストをファイルに書き戻す
			writeFileUTF8( doc_filename, doc_xml );
		}catch( Exception e ){
			e.printStackTrace();
		}
		
		return r;
	}
	
	private static int omitAttachment( String repid, String doc_filename, String author ){
		int r = 0;
		
		try{
			//. ファイルをテキストで読み取る
			String doc_xml = readFileUTF8( doc_filename );

			//. <objectref> があった場合は削除する
			int n = doc_xml.indexOf( "<objectref " );
			while( n > -1 ){
				int n1 = n;
				int n2 = doc_xml.indexOf( "</objectref>", n + 1 );
				if( n1 < n2 ){
					String s1 = doc_xml.substring( 0, n1 );
					String s2 = doc_xml.substring( n2 + 12 );
					doc_xml = s1 + "<i>" + not_supported_msg + "</i>" + s2;
				}

				n = doc_xml.indexOf( "<objectref " );
			}

			//. 添付ファイル
			n = doc_xml.indexOf( "<file " );
			while( n > -1 ){
				int n1 = n;
				int n2 = doc_xml.indexOf( "name='", n1 + 1 );
				int n3 = doc_xml.indexOf( ">", n1 + 1 );
				if( n1 < n2 && n2 < n3 ){
					//. このデータがある文書の UNID を取得する
					String doc_unid = "";
					String tmp_xml = doc_xml.substring( 0, n );
					int t1 = tmp_xml.lastIndexOf( "<noteinfo " );
					int t2 = tmp_xml.indexOf( "unid='", t1 + 1 );
					int t3 = tmp_xml.indexOf( "'", t2 + 6 );
					int t4 = tmp_xml.indexOf( ">", t1 + 1 );
					if( -1 < t1 && t1 < t2 && t2 < t3 && t3 < t4 ){
						doc_unid = tmp_xml.substring( t2 + 6, t3 );
					}
					
					n3 = doc_xml.indexOf( "'", n2 + 6 );
					String file_name = doc_xml.substring( n2 + 6, n3 );
System.out.println( "file_name = " + file_name );
					n1 = doc_xml.indexOf( "<filedata", n1 + 1 );
					n2 = doc_xml.indexOf( ">", n1 + 1 );
					n3 = doc_xml.indexOf( "</filedata", n2 + 1 );
					if( -1 < n1 && n1 < n2 && n2 < n3 ){
						String filedata = doc_xml.substring( n2 + 1, n3 ); //. Base64エンコードされた状態
						String fileurl = fileUpload( repid, doc_unid, file_name, filedata, author );
System.out.println( "fileurl = " + fileurl );
						
						//. </file> を探す
						n1 = doc_xml.indexOf( "</file>", n );
						if( n1 > n ){
							doc_xml = doc_xml.substring( 0, n ) + doc_xml.substring( n1 + 7 );
						}
						
						//. 対応する <attachmentref> を探す
						boolean b = true;
						n2 = -1;
						do{
							n1 = doc_xml.indexOf( "<attachmentref ", n2 + 1 );
							if( n1 > n2 ){
								n2 = doc_xml.indexOf( "name='", n1 + 1 );
								n3 = doc_xml.indexOf( "'", n2 + 6 );
								if( n1 < n2 && n2 < n3 ){
									String fname = doc_xml.substring( n2 + 6, n3 );
System.out.println( "fname = " + fname );
									if( fname.equals( file_name ) ){
System.out.println( "doc_xml0 = " + doc_xml );
										//. 見つかった！　-> 直後の <caption> を変更
										n2 = doc_xml.indexOf( "<caption>", n3 + 1 );
System.out.println( "n2 = " + n2 );
										if( n2 > -1 ){
//											n2 = n2 + 9;
											n3 = doc_xml.indexOf( "</caption>", n2 + 1 );
System.out.println( "n3 = " + n3 );
											if( n3 > n2 ){
System.out.println( "doc_xml1 = " + doc_xml );
												//. ****(n2)<caption>***(n3)</caption>****
												String s1 = doc_xml.substring( 0, n2 );
												String s2 = doc_xml.substring( n2, n3 + 10 );
												String s3 = doc_xml.substring( n3 + 10 );
												doc_xml = s1 + "<br/><a target='_blank' href='" + fileurl + "'>" + s2 + "</a>" + s3;
System.out.println( "doc_xml2 = " + doc_xml );
											}
										}else{
											n2 = doc_xml.indexOf( "<picture ", n1 + 1 );
											if( n2 > n1 ){
												n3 = doc_xml.indexOf( ">", n2 + 1 );
												n2 = doc_xml.indexOf( "</picture>", n3 + 1 );
												//. ****<attachmentref ><picture >(n3+1)***(n2)</picture>***
												String s1 = doc_xml.substring( 0, n3 + 1 );
												String s2 = fname; //doc_xml.substring( n3 + 1, n2 );
												String s3 = doc_xml.substring( n2 );
												
												doc_xml = s1 + "<br/><a target='_blank' href='" + fileurl + "'>" + s2 + "</a>" + s3;
											}											
										}
										
										b = false;
									}
								}else{
									b = false;
								}
								n2 = n3;
							}else{
								b = false;
							}
						}while( b );
						
/*						//. この<file>タグを取り除く （必要？）
						n1 = doc_xml.indexOf( ">", n + 1 );
						n2 = doc_xml.indexOf( "</file>", n1 + 1 );
						if( n < n1 && n1 < n2 ){
							String s1 = doc_xml.substring( 0, n1 ); //. n -> n1
							String s2 = doc_xml.substring( n2 + 7 );
							doc_xml = s1 + s2;
						}
*/
					}
				}
				r ++;
				
				n = doc_xml.indexOf( "<file " );
			}

			//. 添付ファイル（その２）
/*			n = doc_xml.indexOf( "<attachmentref " );
			while( n > -1 ){
				int n2 = doc_xml.indexOf( " name='", n + 1 );
				int n3 = doc_xml.indexOf( "'", n2 + 7 );
				if( n < n2 && n2 < n3 ){
					String fname = doc_xml.substring( n2 + 7, n3 );
				}
				
				n = doc_xml.indexOf( "<attachmentref " );
			}
*/			
			//. テキストをファイルに書き戻す
			writeFileUTF8( doc_filename, doc_xml );
		}catch( Exception e ){
			e.printStackTrace();
		}
		
		
		return r;
	}
		
	private static int replaceSection( String filename ){
		int r = 0;
		
		try{
			//. ファイルをテキストで読み取る
			String xml = readFileUTF8( filename );
			
			int n1 = xml.indexOf( "<section>" );
			while( n1 > -1 ){
				int n2 = xml.indexOf( "</section>", n1 + 1 );
				if( n1 < n2 ){
					String s1 = xml.substring( 0, n1 + 9 );  //. <section> まで
					String s2 = xml.substring( n1 + 9, n2 ); //. <section> ～ </section> の内側
					String s3 = xml.substring( n2 );         //. </section> 以降
					
					int m1 = s2.indexOf( "<sectiontitle " );
					if( m1 > -1 ){
						int m2 = s2.indexOf( "</sectiontitle>", m1 + 1 );
						if( m1 < m2 ){
							r ++;
							String s4 = s2.substring( 0, m1 );       //. おそらく空
							String s5 = s2.substring( m1, m2 + 15 ); //. タイトル部（<sectiontitle> ～ </sectiontitle>）
							String s6 = s2.substring( m2 + 15 );     //. セクション内部
							
							s5 = "<a id='sectionbutton" + r + "' href='#'>" + s5 + "</a>\n";
							s6 = "<div id='section" + r + "'>\n" + s6 + "</div>\n";
							String s7 = "<script type='text/javascript'>\n"
									+ "<!--\n"
									+ "$( '#sectionbutton" + r + "' ).click( function(){\n"
									+ "  $( '#section" + r + "' ).toggle( 'fast' );\n"
									+ "});\n"
									+ "$( '#section" + r + "' ).hide();\n"
									+ "// -->\n"
									+ "</script>\n";
							
							s2 = s4 + s5 + s6 + s7;
							
							xml = s1 + s2 + s3;
						}
					}
				}
				
				n1 = xml.indexOf( "<section>", n1 + 1 );
			}
			
			writeFileUTF8( filename, xml );
		}catch( Exception e ){
			e.printStackTrace();
		}
		
		return r;
	}
	
	private static int replaceName( String filename, View view ){
		int r = 0;
		
		try{
			//. ファイルをテキストで読み取る
			String xml = readFileUTF8( filename );
			
			int n1 = xml.indexOf( "<item " );
			while( n1 > -1 ){
				int n2 = xml.indexOf( " names='true'", n1 + 1 );
				int n3 = xml.indexOf( "</item>", n1 + 1 );
				if( n1 < n2 && n2 < n3 ){
					boolean f = true;
					do{
						int n4 = xml.indexOf( "<text>", n1 + 1 );
						int n5 = xml.indexOf( "</text>", n1 + 1 );
						if( n1 < n4 && n4 < n5 && n5 < n3 ){
							String name = xml.substring( n4 + 6, n5 );
							String s1 = xml.substring( 0, n4 + 6 );
							String s2 = xml.substring( n5 );
							
							lotus.domino.Document doc = view.getFirstDocument();
							boolean b = true;
							while( doc != null && b ){
								//. FullName で比較して、一致していたら InternetAddress に置換
								String fullname = doc.getItemValueString( "FullName" );
								if( fullname.equalsIgnoreCase( name ) ){
									String name1 = doc.getItemValueString( "InternetAddress" );
									//. InternetAddress が定義されていない場合は置換しない
									if( name1 != null && name1.length() > 0 ){
										name = name1;
									}
									
									b = false;
								}
								
								doc = view.getNextDocument( doc );
							}

							xml = s1 + name + s2;
							n1 = n4 + 6 + name.length() + 1;
							n3 = xml.indexOf( "</item>", n1 + 1 );
						}else{
							f = false;
						}
					}while( f );					
				}
				
				n1 = xml.indexOf( "<item ", n3 + 1 );
			}
			
			writeFileUTF8( filename, xml );
		}catch( Exception e ){
			e.printStackTrace();
		}
		
		return r;
	}
	
	private static int omitExtra( String repid, String html_filename, String unid ){
		int r = 0;
		
		try{
			//. ファイルをテキストで読み取る
			String html = readFileUTF8( html_filename );

			//. 添付画像オブジェクト
			int n = html.indexOf( "<picture " );
			while( n > -1 ){
				int n1 = html.indexOf( "<", n + 1 );
				int n2 = html.indexOf( ">", n1 + 1 );
				if( n < n1 && n1 < n2 ){
					//. 画像のフォーマット
					String format = html.substring( n1 + 1, n2 );
					if( format.startsWith( "imageref " ) ){
						//. 画像リソースを参照している
						int n3 = format.indexOf( "name='" );
						int n4 = format.indexOf( "'", n3 + 6 );
						if( -1 < n3 && n3 < n4 ){
							String imgname = format.substring( n3 + 6, n4 );
							
							//. K.Kimura(2013/Nov/18) リンク先要確認
							//String imgurl = imageresource_folder + "/" + imgname;
							String imgurl = unid2wpid.get( imgname );
							
							n3 = html.indexOf( "</imageref>", n2 + 1 );

							String s1 = html.substring( 0, n1 );
							String s2 = html.substring( n3 + 11 );
							html = s1 + "<img src='" + imgurl + "'/>" + s2;
						}
					}else{
						int n3 = format.indexOf( " " );
						if( n3 > -1 ){
							format = format.substring( 0, n3 );
						}
						
						n3 = html.indexOf( "</" + format, n2 + 1 );
						if( n2 < n3 ){
							String picdata = html.substring( n2 + 1, n3 ); //. Base64エンコードされた状態
							String ext = format.equals( "notesbitmap" ) ? "gif" : format; //. ???
							String picurl = picUpload( repid, picdata, unid, ext );

							//. この<format>タグを取り除く
							String s1 = html.substring( 0, n1 );
							String s2 = html.substring( n3 + format.length() + 3 );
							html = s1 + "<img src='" + picurl + "'/>" + s2;
							
							r ++;
						}
					}
				}
					
				n = html.indexOf( "<picture ", n + 1 );
			}
			
			//. リッチテキストの装飾を HTML 対応に変更
			html = swapElement( html, "run", "font" );
			html = html.replaceAll( "<pardef ", "<div " );
			html = html.replaceAll( "</pardef>", "</div>" );
			html = swapElement2( html, "div", "par" ); //. K.Kimura
			html = html.replaceAll( "<tablerow", "<tr" );
			html = html.replaceAll( "</tablerow>", "</tr>" );
			html = html.replaceAll( "<tablecell", "<td" );
			html = html.replaceAll( "</tablecell>", "</td>" );
			html = html.replaceAll( "<urllink ", "<a " );
			html = html.replaceAll( "</urllink>", "</a>" );
			html = html.replaceAll( "<break/>", "<br/>" );
			html = html.replaceAll( "<break></break>", "<br/>" );
			html = html.replaceAll( " style='bold", " style='font-weight:bold;" );
			
			//. ノーツリンクの再現
			
			//. 余分なマクロコードを取り除く
			html = omitCode( html );
			
			//. font タグ内の name 属性を取り除く
			html = omitFontName( html );
			
			//. 埋め込まれた HTML を復元
			html = regenerateEmbeddedHTML( html );
			
			//. テキストをファイルに書き戻す
			writeFileUTF8( html_filename, html );
		}catch( Exception e ){
			e.printStackTrace();
		}
		
		return r;
	}
	
	private static String omitCode( String html ){
		try{
			html = omitTag( html, "code" );
			html = omitTag( html, "compositedata" );
			html = omitTag( html, "actionhotspot" );
		}catch( Exception e ){
			e.printStackTrace();
		}
		
		return html;
	}
	
	private static String omitFontName( String html ){
		try{
			int n1 = html.indexOf( "<font " );
			while( n1 > -1 ){
				int n2 = html.indexOf( " name='", n1 + 1 );
				int n3 = html.indexOf( ">", n1 + 1 );
				if( n1 < n2 && n2 < n3 ){
					int n4 = html.indexOf( "'", n2 + 7 );
					if( n2 + 7 < n4 && n4 < n3 ){
						String s1 = html.substring( 0, n2 );
						String s2 = html.substring( n4 + 1 );
						html = s1 + s2;
					}
				}
				
				n1 = html.indexOf( "<font ", n1 + 1 );
			}
		}catch( Exception e ){
			e.printStackTrace();
		}
		
		return html;
	}

	private static String omitTag( String html, String tag ){
		try{
			int n1 = html.indexOf( "<" + tag );
			while( n1 > -1 ){
				int n2 = html.indexOf( "</" + tag + ">", n1 + 1 );
				if( n1 < n2 ){
					String s1 = html.substring( 0, n1 );
					String s2 = html.substring( n2 + tag.length() + 3 );
					html = s1 + s2;
				}else{
					n1 ++;
				}
				
				n1 = html.indexOf( "<" + tag, n1 );
			}
		}catch( Exception e ){
			e.printStackTrace();
		}
		
		return html;
	}
	
	
	private static void extractTag( String filename, String[] tags ){
		try{
			String xml = readFileUTF8( filename );
			
			//. 
			int n0 = xml.indexOf( "<?" );  //. <?xml
			int n1 = xml.indexOf( "<", n0 + 1 ); //. ルートタグ
			if( -1 < n0 && n0 < n1 ){
				//. ルートタグ名
				int n11 = xml.indexOf( " ", n1 + 1 );
				int n12 = xml.indexOf( ">", n1 + 1 );
				int n10 = ( n11 < n12 ) ? n11 : n12;
				String roottagname = xml.substring( n1 + 1, n10 );

				String s1 = xml.substring( 0, n12 + 1 );
				
				int n9 = xml.lastIndexOf( "</" + roottagname );
				String s2 = xml.substring( n9 );

				String xml0 = "";
				for( int i = 0; i < tags.length; i ++ ){
					int n2 = xml.indexOf( "<" + tags[i], n1 + 1 ); //. 目的のタグ
					int n3 = xml.indexOf( "</" + tags[i] + ">", n2 + 1 );
					while( n1 < n2 && n2 < n3 ){
						xml0 += ( xml.substring( n2, n3 + tags[i].length() + 3 ) + "\n" );
						
						n2 = xml.indexOf( "<" + tags[i], n3 + 1 );
						n3 = xml.indexOf( "</" + tags[i] + ">", n2 + 1 );
					}					
				}
				
				xml = s1 + xml0 + s2;
			}
			
			writeFileUTF8( filename, xml );
		}catch( Exception e ){
			e.printStackTrace();
		}
	}

	private static String regenerateEmbeddedHTML( String html ){
		try{
			//. <font ..><run html="true"></run>***</font> となっているパターンの場合、
			//. **** 部分は HTML の一部なので URL デコードして HTML に戻し、かつタグを全て取り除く
			
			//. 問題は HTML の一部の中にタグとして残っている箇所と、既にサニタイズされている箇所が共存していること。
			//. 全てサニタイズで統一するとタグが消える。
			//. かといって全てアンサニタイズするとタグの順序が乱れる。
			//. ではそのまま残して大丈夫？？
			
			int n1 = html.indexOf( "<font" );
			while( n1 > -1 ){
				boolean b = false;
				int n0 = html.indexOf( "<", n1 + 1 );
				int n2 = html.indexOf( "<run ", n1 + 1 );
				if( n1 < n2 && n0 == n2 ){
					n0 = html.indexOf( "html='true'", n2 + 1 );
					int n3 = html.indexOf( ">", n2 + 1 );
					if( n2 < n0 && n0 < n3 ){
						n0 = html.indexOf( "<", n3 + 1 );
						int n4 = html.indexOf( "</run>", n3 + 1 );
						if( n3 < n4 && n0 == n4 ){
							int n5 = html.indexOf( "</font>", n4 + 1 );
							if( n4 < n5 ){
								//. 条件が揃った！
								b = true;
								
								//. ++(n1)<font ..>++(n2)<run html="true">(n3)++(n4)</run>***(n5)</font> となっているパターンの場合、
								String s1 = html.substring( 0, n1 );
								String s2 = html.substring( n4 + 6, n5 ); //. 上の "***" の部分（ここにタグが含まれている可能性がある・・）
								String s3 = html.substring( n5 + 7 );
///								html = s1 + unsanitize( URLDecoder.decode( s2, "UTF-8" ) ) + s3; //. unsanitize の必要ある？？ K.Kimura(2011/Apr/26)
								html = s1 + s2 + s3;
							}
						}
					}
				}

				if( !b ){
					n1 ++;
				}
				
				n1 = html.indexOf( "<font", n1 );
			}
		}catch( Exception e ){
			e.printStackTrace();
		}
		
		return html;
	}


	public static String getUnid( Element element ){
		String unid = "";
		
		try{
			NodeList noteinfoList = element.getElementsByTagName( "noteinfo" );
			Element noteinfoElement = ( Element )noteinfoList.item( 0 );
			unid = noteinfoElement.getAttribute( "unid" );
		}catch( Exception e ){
		}

		if( unid == null || unid.length() == 0 ){
			unid = "(nounid)";
		}
		
		return unid;
	}
	
	public static Element getRootElement( String xml ){
		Element root = null;
		
		try{
			DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = dbfactory.newDocumentBuilder();
			Document xdoc = builder.parse( new InputSource( new StringReader( xml ) ) );
			root = xdoc.getDocumentElement();
		}catch( Exception e ){
			e.printStackTrace();
		}
		
		return root;
	}
	
	public static Element getRootElement( File file ){
		Element root = null;
		
		try{
			DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = dbfactory.newDocumentBuilder();
			Document xdoc = builder.parse( file );
			root = xdoc.getDocumentElement();
		}catch( Exception e ){
			e.printStackTrace();
		}
		
		return root;
	}

	private static String fileUpload( String repid, String doc_unid, String filename, String b64filedata, String author ){
		String url = null;
		
		try{
			//. Base64 デコード
//			byte[] filebin = Base64.decodeBase64( b64filedata );
//			byte[] filebin = ( new BASE64Decoder() ).decodeBuffer( b64filedata );
			byte[] filebin = B64decode( b64filedata );
			
			//. モバイル化対応
			String folder = html_folder;
			
			//. クラウドなどにアップロード
			createFolder( repid + "/" + folder + "/" + file_folder );
			createFolder( repid + "/" + folder + "/" + file_folder + "/" + doc_unid );
			String fname = repid + "/" + folder + "/" + file_folder + "/" + doc_unid + "/" + filename;
			try{
				FileOutputStream fos = new FileOutputStream( fname );
				fos.write( filebin, 0, filebin.length );
				fos.flush();
				fos.close();
			}catch( Exception e ){
				e.printStackTrace();
			}
			
			File file = new File( fname );
			url = postFile( repid, file, author );
		}catch( Exception e ){
			e.printStackTrace();
		}
		
		return url;
	}
	
	private static String picUpload( String repid, String b64picdata, String unid, String ext ){
		String url = null;
		
		try{
			if( ext == null || ext.length() == 0 ){
				ext = "gif";
			}
			
			//. Base64 デコード
//			byte[] picbin = Base64.decodeBase64( b64picdata );
			byte[] picbin = B64decode( b64picdata );
			
			//. モバイル化対応
			String folder = html_folder;
			
			//. クラウドなどにアップロード
			createFolder( repid + "/" + folder + "/" + picture_folder );
			int i = -1;
			String fname = "";
			boolean b = true;
			do{
				i ++;
				fname = repid + "/" + folder + "/" + picture_folder + "/" + unid + "_" + i + "." + ext;
				File f = new File( fname );
				b = f.exists();
			}while( b );
			
			try{
				FileOutputStream fos = new FileOutputStream( fname );
				fos.write( picbin, 0, picbin.length );
				fos.flush();
				fos.close();
			}catch( Exception e ){
				e.printStackTrace();
			}

			//. アップロード先へのリンク
			File file = new File( fname );
			url = postFile( repid, file, author_id );
			//url = picture_folder + "/" + unid + "_" + i + "." + ext;
		}catch( Exception e ){
			e.printStackTrace();
		}
		
		return url;
	}
	
	private static String imageresourceUpload( String repid, String b64imgdata, String filename ){
		String url = null;
		
		try{
			byte[] imgbin = B64decode( b64imgdata );
			
			//. クラウドなどにアップロード
			createFolder( repid + "/" + html_folder + "/" + imageresource_folder );
			String fname = repid + "/" + html_folder + "/" + imageresource_folder + "/" + filename;
			
			try{
				FileOutputStream fos = new FileOutputStream( fname );
				fos.write( imgbin, 0, imgbin.length );
				fos.flush();
				fos.close();
			}catch( Exception e ){
				e.printStackTrace();
			}

			//. アップロード先へのリンク
			url = imageresource_folder + "/" + filename;
		}catch( Exception e ){
			e.printStackTrace();
		}
		
		return url;
	}
	
	
	private static void WriteElementToXmlFile( String filename, Element element ){
		try{
			BufferedWriter writer = new BufferedWriter( new OutputStreamWriter( new FileOutputStream( filename ), "UTF-8" ) );
//			File file = new File( filename );
//			FileWriter writer = new FileWriter( file );
			
//			writer.write( "<?xml version='1.0' encoding='" + xml_encode + "'?>\n" );
			writer.write( "<?xml version='1.0' ?>\n" );
			
			traceWriteElement( writer, element, true );
			
			writer.close();
		}catch( Exception e ){
			e.printStackTrace();
		}
	}
	
	private static void traceWriteElement( BufferedWriter writer, Node node, boolean isTop ){
		try{
			int type = node.getNodeType();  //. sharedaction の時、ここで NullPointerException（まあいいか・・）
			if( type == Node.ELEMENT_NODE ){
				//. 自分自身
				String tagname = ( ( Element )node ).getTagName();
				writer.write( "<" + tagname );

				//. 属性
				NamedNodeMap attrs = node.getAttributes();
				if( attrs != null ){
					for( int i = 0; i < attrs.getLength(); i ++ ){
						Node attr = attrs.item( i );
						String attrName = attr.getNodeName();
						String attrValue = sanitize( attr.getNodeValue() );
						writer.write( " " + attrName + "='" + attrValue + "'" );
					}
				}
				
				writer.write( ">" );

				//. 子
				if( node.hasChildNodes() ){
					Node child = node.getFirstChild();
					traceWriteElement( writer, child, false ) ;
				}
				
				if( type == Node.ELEMENT_NODE ){
					writer.write( "</" + tagname + ">" );
				}
			}else if( type == Node.TEXT_NODE ){
				try{
					String text = node.getNodeValue();
					if( text != null ){
						writer.write( sanitize( text ) );
					}
				}catch( Exception e ){
					e.printStackTrace();
				}
			}			
			
			//. 兄弟
			if( !isTop ){
				Node sib = node.getNextSibling();
				if( sib != null ){
					traceWriteElement( writer, sib, false );
				}
			}
		}catch( Exception e ){
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unused")
	private static void generateFormXsl( String repid, String form_xml ){
		try{
			Element formElement = getRootElement( form_xml ); // <form>

			String form_unid = "";
			try{
				form_unid = getUnid( formElement );
			}catch( Exception e ){
			}
			
			//. 背景色
			String form_bgcolor = null;
			int n1 = form_xml.indexOf( "<form " );
			if( n1 > -1 ){
				int n2 = form_xml.indexOf( "bgcolor='", n1 + 1 );
				int n3 = form_xml.indexOf( "'", n2 + 9 );
				int n4 = form_xml.indexOf( ">", n1 + 1 );
				if( n1 < n2 && n2 < n3 && n3 < n4 ){
					form_bgcolor = form_xml.substring( n2 + 9, n3 );
				}
			}
			
			//. ボディ
			String form_body = "";
			n1 = form_xml.indexOf( "<richtext" );
			int n2 = form_xml.lastIndexOf( "</richtext>" );
			if( -1 < n1 && n1 < n2 ){
				form_body = form_xml.substring( n1, n2 + 11 );
			}
/*			
			String form_xsl = "<xsl:template match='/'>\n"
				+ "<html>\n"
				+ "<head>\n"
				+ "<title><xsl:value-of select='/document/noteinfo/@unid'/></title>\n"
				+ "</head>\n"
				+ "<body";
			if( form_bgcolor != null && form_bgcolor.length() > 0 ){
				form_xsl += ( " bgcolor=\"" + form_bgcolor + "\"" );
			}
			form_xsl += ">\n"
				+ form_body + "\n"
				+ "</body>\n"
				+ "</html>\n"
				+ "</xsl:template>\n\n";

			//. XSL のテンプレート用修正
			n1 = form_xsl.indexOf( "<field " );
			while( n1 > -1 ){
				n2 = form_xsl.indexOf( ">", n1 + 1 );
				if( n2 > n1 ){
					String field_name = "", field_type = "";
					int n01 = form_xsl.indexOf( "name='", n1 + 1 );
					if( n01 > -1 ){
						int n02 = form_xsl.indexOf( "'", n01 + 6 );
						if( n02 > n01 ){
							field_name = form_xsl.substring( n01 + 6, n02 );
						}
					}
					
					n01 = form_xsl.indexOf( "type='", n1 + 1 );
					if( n01 > -1 ){
						int n02 = form_xsl.indexOf( "'", n01 + 6 );
						if( n02 > n01 ){
							field_type = form_xsl.substring( n01 + 6, n02 );
						}
					}
					
					String xsl_type = ( field_type.equals( "text" ) ) ? "value-of" : "copy-of";

					String s1 = form_xsl.substring( 0, n1 );
					String s2 = form_xsl.substring( n2 + 1 );
					form_xsl = s1 + "<xsl:" + xsl_type + " select='/document/item[@name=\"" + field_name + "\"]'/>" + s2;
					
					n1 = form_xsl.indexOf( "<field " );
				}
			}

			//. XSL 内の余分なタグを消去
			form_xsl = form_xsl.replaceAll( "</field[^>]*>", "" );
			form_xsl = form_xsl.replaceAll( "<compositedata[^<]*</compositedata>", "" );
			form_xsl = form_xsl.replaceAll( "<compositedata[^>]*>", "" );
			form_xsl = form_xsl.replaceAll( "</compositedata>", "" );

			//. XSL 内のタグを調整
			//form_xsl = swapElement( form_xsl, "run", "font" );

			form_xsl = swapNotesLinks( form_xsl );

			while( form_xsl.indexOf( "<notesbitmap" ) > -1 ){
				//. ペーストされた画像は切り取る
				form_xsl = omitElement( form_xsl, "notesbitmap" );
			}
			
			form_xsl = form_xsl.replaceAll( "<par ", "<p " );
			form_xsl = form_xsl.replaceAll( "</par>", "</p>" );
*/
			//. XSL のテンプレート用修正
			n1 = form_body.indexOf( "<field " );
			while( n1 > -1 ){
				n2 = form_body.indexOf( ">", n1 + 1 );
				if( n2 > n1 ){
					String field_name = "", field_type = "";
					int n01 = form_body.indexOf( "name='", n1 + 1 );
					if( n01 > -1 ){
						int n02 = form_body.indexOf( "'", n01 + 6 );
						if( n02 > n01 ){
							field_name = form_body.substring( n01 + 6, n02 );
						}
					}
					
					n01 = form_body.indexOf( "type='", n1 + 1 );
					if( n01 > -1 ){
						int n02 = form_body.indexOf( "'", n01 + 6 );
						if( n02 > n01 ){
							field_type = form_body.substring( n01 + 6, n02 );
						}
					}
					
					String xsl_type = ( field_type.equals( "text" ) ) ? "value-of" : "copy-of";

					String s1 = form_body.substring( 0, n1 );
					String s2 = form_body.substring( n2 + 1 );
					form_body = s1 + "<xsl:" + xsl_type + " select='/document/item[@name=\"" + field_name + "\"]'/>" + s2;
					
					n1 = form_body.indexOf( "<field " );
				}
			}

			//. XSL 内の余分なタグを消去
			form_body = form_body.replaceAll( "</field[^>]*>", "" );
			form_body = form_body.replaceAll( "<compositedata[^<]*</compositedata>", "" );
			form_body = form_body.replaceAll( "<compositedata[^>]*>", "" );
			form_body = form_body.replaceAll( "</compositedata>", "" );

			//. XSL 内のタグを調整
			//form_xsl = swapElement( form_xsl, "run", "font" );

			form_body = swapNotesLinks( repid, form_body );

			while( form_body.indexOf( "<notesbitmap" ) > -1 ){
				//. ペーストされた画像は切り取る
				form_body = omitElement( form_body, "notesbitmap" );
			}
			
			form_body = form_body.replaceAll( "<par ", "<p class=\"par\" " );
			form_body = form_body.replaceAll( "</par>", "</p>" );

			//. PC 用 XSL
			String form_xsl = "<xsl:template match='/'>\n"
				+ "<html>\n"
				+ "<head>\n"
				+ "<title><xsl:value-of select='/document/noteinfo/@unid'/></title>\n"
				+ "</head>\n"
				+ "<body";
			if( form_bgcolor != null && form_bgcolor.length() > 0 ){
				form_xsl += ( " bgcolor=\"" + form_bgcolor + "\"" );
			}
			form_xsl += ">\n"
				+ form_body + "\n"
				+ "</body>\n"
				+ "</html>\n"
				+ "</xsl:template>\n\n";

			form_xsl = "<?xml version='1.0' ?>\n"
					+ "<xsl:stylesheet version='1.0' xmlns:xsl='http://www.w3.org/1999/XSL/Transform'>\n"
					+ "<xsl:output method='html' encoding='UTF-8'/>\n"
					+ form_xsl
					+ "</xsl:stylesheet>\n";

			//. XSL ファイルを保存			
			String filename = repid + "/" + html_folder + "/" + form_unid + ".xsl";
			writeFileUTF8( filename, form_xsl );
			
			
			//. モバイル用 XSL
			String form_xsl_m = "<xsl:template match='/'>\n"
				+ "<html>\n"
				+ "<head>\n"
				+ "<title><xsl:value-of select='/document/noteinfo/@unid'/></title>\n"

				+ "<meta name='viewport' content='width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no'/>\n"
				+ "<meta name='apple-mobile-web-app-capable' content='yes' /> \n"
				+ "<script type='text/javascript'>\n"
				+ "<!--\n"
				+ "  window.onload = function(){\n"
				+ "    setTimeout( scrollTo, 100, 0, 1 );\n"
				+ "  }\n"
				+ "// -->\n"
				+ "</script>\n"
				+ "<link href='js/dojox/mobile/themes/" + mobile_css + "/" + mobile_css + ".css' rel='stylesheet'></link>\n"
				+ "<script type='text/javascript' src='js/dojo/dojo.js' djConfig='parseOnLoad: true'></script>\n"
				+ "<script language='JavaScript' type='text/javascript'>\n"
				+ "<!--\n"
				+ "dojo.require( 'dojox.mobile.parser' );\n"
				+ "dojo.require( 'dojox.mobile' );\n"
				+ "dojo.require( 'dojox.mobile.compat' );\n"
				+ "// -->\n"
				+ "</script>\n"

				+ "</head>\n"
				+ "<body>\n"
				
				+ "<div id='settings' dojoType='dojox.mobile.View' selected='true'>\n"
				+ "<h1 align='middle' dojoType='dojox.mobile.Heading' back='Back' href='javascript:history.back();'>" + "</h1>\n"
				+ form_body + "\n"
				
				+ "</div>\n"
				
				+ "</body>\n"
				+ "</html>\n"
				+ "</xsl:template>\n\n";

			form_xsl_m = "<?xml version='1.0' ?>\n"
					+ "<xsl:stylesheet version='1.0' xmlns:xsl='http://www.w3.org/1999/XSL/Transform'>\n"
					+ "<xsl:output method='html' encoding='UTF-8'/>\n"
					+ form_xsl_m
					+ "</xsl:stylesheet>\n";

			//. XSL ファイルを保存			
			String mfilename = repid + "/" + html_folder + "/m" + form_unid + ".xsl";
			writeFileUTF8( mfilename, form_xsl_m );


		}catch( Exception e ){
			e.printStackTrace();
		}
	}

	/*
	 * K.Kimura 2011/Apr/13
	 * カテゴライズビューの再現にまだ問題あり
	 */
	@SuppressWarnings("unchecked")
	private static void generateViewXsl( String repid, String view_xml ){
		try{
			Element viewElement = getRootElement( view_xml ); // <form>

			
			//. 名前
			String view_name = "";
			try{
				view_name = viewElement.getAttribute( "name" );
			}catch( Exception e ){
			}

			//. View UNID
			String view_unid = "";
			try{
				view_unid = getUnid( viewElement );
			}catch( Exception e ){
			}

			//. 背景色
			String view_bgcolor = null;
			int n1 = view_xml.indexOf( "<view " );
			if( n1 > -1 ){
				int n2 = view_xml.indexOf( "bgcolor='", n1 + 1 );
				int n3 = view_xml.indexOf( "'", n2 + 9 );
				int n4 = view_xml.indexOf( ">", n1 + 1 );
				if( n1 < n2 && n2 < n3 && n3 < n4 ){
					view_bgcolor = view_xml.substring( n2 + 9, n3 );
				}
			}
			
			//. 選択式
			@SuppressWarnings("unused")
			boolean isAll = false;
			String select_form = null;
			try{
				NodeList codeList = viewElement.getElementsByTagName( "code" );
				Element codeElement = ( Element )codeList.item( 0 );
				String event = codeElement.getAttribute( "event" );
				if( event.equals( "selection" ) ){
					NodeList formulaList = codeElement.getElementsByTagName( "formula" );
					Element formulaElement = ( Element )formulaList.item( 0 );
					String formula = formulaElement.getFirstChild().getNodeValue().trim(); //. ビューの選択式
					if( formula.toLowerCase().startsWith( "select form" ) ){
						formula = formula.substring( 11 );
						
						n1 = formula.indexOf( "=" );
						if( n1 > -1 ){
							formula = formula.substring( n1 + 1 );
							formula = formula.replaceAll( "\"", "'" );
							
							n1 = formula.indexOf( "'" );
							int n2 = formula.indexOf( "'", n1 + 1 );
							if( -1 < n1 && n1 < n2 ){
								select_form = formula.substring( n1 + 1, n2 );
							}
						}
					}else if ( formula.toLowerCase().startsWith( "select @all" ) ){
						isAll = true;
					}
				}
			}catch( Exception e ){
			}
			
			//. フォーム一覧と、デフォルト表示対象を取得
			HashMap<String, String[]> formmap = new HashMap<String, String[]>();
			String formunid = "";

			//. Form UNID を求めておく
			boolean b = true;
			File form_xml_dir = new File( repid + "/" + form_xml_folder );
			String def_formid = null;
			if( form_xml_dir.isDirectory() ){
				File[] form_xml_files = form_xml_dir.listFiles();
				for( int i = 0; i < form_xml_files.length && b; i ++ ){
					String form_xml_filename = form_xml_files[i].getAbsolutePath();

					try{
///						String form_dxl = readFileUTF8( form_xml_filename );
///						System.out.println( "form_dxl = " + form_dxl );
///						Element formElement = getRootElement( form_dxl );  //. ここで解析エラーが出る前提が必要
						Element formElement = getRootElement( new File( form_xml_filename ) );
						
						//. 返答文書用フォームは対象外
						String type = null;
						try{
							type = formElement.getAttribute( "type" );
						}catch( Exception e ){
						}
							
						if( type == null || type.length() == 0 ){
							String formid = getUnid( formElement );
							
							String defform = formElement.getAttribute( "default" );
							if( defform != null && defform.equals( "true" ) ){
								def_formid = formid;
							}
							
							String formname = formElement.getAttribute( "name" ), formalias = null;
							try{
								formalias = formElement.getAttribute( "alias" );
							}catch( Exception e ){
							}

							int m1 = 0, m2 = 0;
							String[] fm1 = null, fm2 = null;
							if( formname != null && formname.length() > 0 ){
								fm1 = formname.split( "\\|" );
								m1 = fm1.length;
							}
							if( formalias != null && formalias.length() > 0 ){
								fm2 = formalias.split( "\\|" );
								m2 = fm2.length;
							}
								
	///						System.out.println( "formname = '" + formname + "', m1 = " + m1 );
	///						System.out.println( "formalias = '" + formalias + "', m2 = " + m2 );
								
							String[] formnames = new String[m1+m2];
							for( int j = 0; j < m1; j ++ ){
								formnames[j] = fm1[j].trim();

								if( select_form != null && select_form.length() > 0 && formnames[j].equalsIgnoreCase( select_form ) ){
									formunid = formid;
										
									b = false;
								}
							}
							for( int j = 0; j < m2; j ++ ){
								formnames[m1+j] = fm2[j].trim();

								if( select_form != null && select_form.length() > 0 && formnames[m1+j].equalsIgnoreCase( select_form ) ){
									formunid = formid;
										
									b = false;
								}
							}
								
							formmap.put( formid, formnames );
						}
					}catch( Exception e ){
						//. XML の解析エラーがでる場合もそこだけ無視
						DbgPrintln( "Parse Exception" + form_xml_filename + "): " + e );
					}
				}
			}
			
			if( b && def_formid != null ){
				//. ビューにフォーム式の定義がない場合は、デフォルトフォームの ID を formunid に設定する
				formunid = def_formid;
			}
			
			//. 各列の属性を調べる
			int columnNum = -1;
			
			String[] cItemName = null, cTitle = null, cResOnly = null, cWidth = null, cFormula = null, cCategorized = null, cHidden = null;
			try{
				NodeList columnList = viewElement.getElementsByTagName( "column" );
				columnNum = columnList.getLength();
				cItemName = new String[columnNum];
				cTitle = new String[columnNum];
				cResOnly = new String[columnNum];
				cWidth = new String[columnNum];
				cFormula = new String[columnNum];
				cCategorized = new String[columnNum];
				cHidden = new String[columnNum];
				for( int i = 0; i < columnNum; i ++ ){
					Element columnElement = ( Element )columnList.item( i );
					cItemName[i] = columnElement.getAttribute( "itemname" );
					cResOnly[i] = columnElement.getAttribute( "responsesonly" );

					cWidth[i] = columnElement.getAttribute( "width" );
					cTitle[i] = cFormula[i] = cCategorized[i] = cHidden[i] = "";

					try{
						cCategorized[i] = columnElement.getAttribute( "categorized" );
					}catch( Exception e ){
					}

					try{
						cHidden[i] = columnElement.getAttribute( "hidden" );
					}catch( Exception e ){
					}

					try{
						NodeList headerList = columnElement.getElementsByTagName( "columnheader" );
						Element headerElement = ( Element )headerList.item( 0 );
						cTitle[i] = headerElement.getAttribute( "title" );
					}catch( Exception e ){
					}
					if( cTitle[i] == null || cTitle[i].length() == 0 ){
						cTitle[i] = cItemName[i];
					}
					
					if( cItemName[i].startsWith( "$" ) ){
						try{
							NodeList codeList = columnElement.getElementsByTagName( "code" );
							Element codeElement = ( Element )codeList.item( 0 );
							NodeList formulaList = codeElement.getElementsByTagName( "formula" );
							Element formulaElement = ( Element )formulaList.item( 0 );
							cFormula[i] = formulaElement.getFirstChild().getNodeValue();
						}catch( Exception e ){
						}
					}
				}
			}catch( Exception e ){
				e.printStackTrace();
			}
			
			//. PC 用 XSL
			String view_xsl = "<xsl:template match='/'>\n"
				+ "<html>\n"
				+ "<head>\n"
				+ "<title>" + view_name + "</title>\n"
				
				+ "<link rel='shortcut icon' type='image/gif' href='" + picture_folder + "/" + sys_imgs_dbicon + "'/>\n"

				+ "<style type='text/css'>\n"

				//. 垂直スクロールバー対応テーブル
				+ ".scr{\n"
				+ "  overflow: scroll;\n"
				+ "  height: 80%;"
				+ "}\n"
				
				//. 見出しを水平線で囲む
				+ "h1{\n"
				+ "  border-top: 1px solid black;\n"
				+ "  margin: 23px 0 0;\n"
				+ "  text-align: center;\n"
				+ "  padding: 0;\n"
				+ "  height: 24px;\n"
				+ "}\n"
				+ "h1 span{\n"
				+ "  position: relative;\n"
				+ "  top: -24px;\n"
				+ "  padding: 0 20px;\n"
				+ "  background: ";
			
			//. 水平線で囲まれた見出しの背景色を指定（これをしないと水平線と見出しが重なる）
			if( view_bgcolor != null && view_bgcolor.length() > 0 ){
				view_xsl += view_bgcolor;
			}else{
				view_xsl += "white";
			}			
			
			view_xsl += ";\n"
				+ "}\n";
				
			//. リンクの下線を表示しない
			view_xsl += "\n"
				+ "a{\n"
				+ "  text-decoration: none;\n"
				+ "}\n"
				+ "</style>\n";

			view_xsl += "<script type='text/javascript' src='js/jquery.js'></script>\n";

/*			view_xsl += "<link rel='stylesheet' href='js/jquery.treeview.css' />\n"
				+ "<script src='js/jquery.treeview.js' type='text/javascript'></script>\n"
				+ "<script type='text/javascript'>\n"
				+ "<!-- \n"
				+ "$( document ).ready( function(){\n"
				+ " $( '#" + view_unid + "' ).treeview();\n"
				+ "});\n"
				+ "// -->\n"
				+ "</script>\n";
*/				
			view_xsl += "<link rel='stylesheet' href='js/jqtreetable.css' />\n"
				+ "<script src='js/jqtreetable.js' type='text/javascript'></script>\n";
			
			view_xsl += "</head>\n"
				+ "<body";
			if( view_bgcolor != null && view_bgcolor.length() > 0 ){
				view_xsl += ( " bgcolor='" + view_bgcolor + "'" );
			}
			view_xsl += ">\n";
			
			String select = "<select name='formunid'>\n";
			for( Iterator it = formmap.entrySet().iterator(); it.hasNext(); ){
				Map.Entry entry = ( Map.Entry )it.next();
				String formid = ( String )entry.getKey();
				String[] formnames = ( String[] )entry.getValue();
				select += ( "<option value='" + formid + "'" );
				if( formid.equals( formunid ) ){
					select += ( " selected='selected'" );
				}
				select += ( ">" + formnames[0] + "</option>\n" );
			}
			select += "</select>\n";

			view_xsl += "<form method='get' target='preview' action='/getdoc'>\n"
				+ "<div align='right'>" + select + "<input type='submit' value='New'/></div>\n"
				+ "<input type='hidden' name='repid' value='" + repid + "'/>\n"
				+ "<input type='hidden' name='e' value='1'/>\n"
				+ "<input type='hidden' name='views' value='" + view_unid + "'/>\n"
				+ "</form>\n";
			
//.			view_xsl += "<h1>" + view_name + "</h1>\n";
			view_xsl += "<h1><span>" + view_name + "</span></h1>\n";  //. 見出しを水平線で囲む
			
			view_xsl += "<xsl:apply-templates select='/database'/>\n";
				
			view_xsl += "\n"
				+ "</body>\n"
				+ "</html>\n"
				+ "</xsl:template>\n\n";

			view_xsl += "<xsl:template match='/database'>\n"
				+ "<div class='scr'>\n"
				+ "<table>\n" //"<ul id='" + view_unid + "' class='treeview'>\n"
				+ "<tbody id='" + view_unid + "'>\n"
				+ "<xsl:apply-templates select='document'/>\n"
				+ "</tbody>\n"
				+ "</table>\n" //"</ul>\n"
				+ "</div>\n"

				+ "<script type='text/javascript'>\n"
				+ "<!-- \n"
				+ "$( document ).ready( function(){\n"
				+ " var map = null;\n"
				+ " var opt = { openImg: 'js/images/tv-collapsable.gif', shutImg: 'js/images/tv-expandable.gif', leafImg: 'js/images/tv-item.gif', lastOpenImg: 'js/images/tv-collapsable-last.gif', lastShutImg: 'js/images/tv-expandable-last.gif', lastLeafImg: 'js/images/tv-item-last.gif', vertLineImg: 'js/images/vertline.gif', blankImg: 'js/images/blank.gif', collapse: false, column: 1, striped: true, highlight: true, state:true};\n"
				+ " $( '#" + view_unid + "' ).jqTreeTable( map, opt );\n"
				+ "});\n"
				+ "// -->\n"
				+ "</script>\n"
				
				+ "</xsl:template>\n\n";
			
			view_xsl += "<xsl:template name='doc' match='document'>\n";
			
			//. 各 document ごとのデータを <li> ～ </li> 内で指定

			//. K.Kimura(2011/Dec/26)
			String viewLi = "<tr>\n" //"<li>\n"
					+ "<td>\n"
					+ "<input>\n"
					+ "<xsl:attribute name='name'>docs</xsl:attribute>\n"
					+ "<xsl:attribute name='type'>radio</xsl:attribute>\n"
					+ "<xsl:attribute name='onClick'>\n"
					+ "window.parent.preview.location.href=\"<xsl:value-of select='./noteinfo/@unid'/>.xml\"\n"
					+ "</xsl:attribute>\n"
					+ "</input>\n"
					+ "</td>\n";
			for( int i = 0; i < columnNum; i ++ ){
				if( !cHidden[i].equals( "true" ) ){
					//. この列の内容として表示させるもの
					String colvalue = "";
					if( cItemName[i].startsWith( "$" ) ){
						String formula = cFormula[i].toLowerCase();
						if( formula.equals( "@created" ) ){
							colvalue = "<xsl:apply-templates select='./noteinfo/created/datetime'/>";
						}else if( formula.equals( "@modified" ) ){	
							colvalue = "<xsl:apply-templates select='./noteinfo/modified/datetime'/>";
						}else if( formula.equals( "@username" ) ){	
							colvalue = "<xsl:apply-templates select='./noteinfo/updatedby/datetime'/>";
						}else{
							colvalue = "<xsl:copy-of select='./item[@name=\"" + cTitle[i] + "\"]'/>";
						}
					}else{
						colvalue = "<xsl:copy-of select='./item[@name=\"" + cItemName[i] + "\"]'/>";
					}
					
					if( cCategorized[i].equals( "true" ) ){
						//. カテゴリ列 -> 展開可能にしたい
						
					}
					
					viewLi += ( "<td>" + colvalue + "<xsl:text>  </xsl:text></td>\n" );
					
					//. スレッド
					if( cResOnly[i].equals( "true" ) ){
						i = columnNum;
					}
				}
			}

			
//			viewLi += "<ul><xsl:for-each select='./document'><xsl:call-template name='doc'/></xsl:for-each></ul>\n"; //. 子 document を再帰的に
			viewLi += "</tr>\n"; //"</li>\n";
			
			view_xsl += viewLi;
			
			view_xsl += "\n"
				+ "</xsl:template>\n";
			
			view_xsl += "\n<xsl:template match='datetime'>\n"
				+ "<xsl:value-of select='substring( ., 1, 4 )'/>"
				+ "/<xsl:value-of select='substring( ., 5, 2 )'/>"
				+ "/<xsl:value-of select='substring( ., 7, 2 )'/>"
				+ "<xsl:text> </xsl:text>"  //. 空白を強制的に挿入
				+ "<xsl:value-of select='substring( ., 10, 2 )'/>"
				+ ":<xsl:value-of select='substring( ., 12, 2 )'/>"
				+ ":<xsl:value-of select='substring( ., 14, 2 )'/>\n"
				+ "</xsl:template>\n";
			
			//. XSL 内のタグを調整			
			view_xsl = "<?xml version='1.0' ?>\n"
					+ "<xsl:stylesheet version='1.0' xmlns:xsl='http://www.w3.org/1999/XSL/Transform'>\n"
					+ "<xsl:output method='html' encoding='UTF-8'/>\n"
					+ view_xsl
					+ "</xsl:stylesheet>\n";

			//. XSL ファイルを保存
			String filename = repid + "/" + html_folder + "/" + view_unid + ".xsl";
			writeFileUTF8( filename, view_xsl );
			
			
			//. モバイル用 XSL
			String mview_xsl = "<xsl:template match='/'>\n"
				+ "<html>\n"
				+ "<head>\n"
				+ "<title>" + view_name + "</title>\n"
				+ "<link rel='shortcut icon' type='image/gif' href='" + picture_folder + "/" + sys_imgs_dbicon + "'/>\n"

				+ "<link href='js/jquery.mobile-1.0a4.1.min.css' rel='stylesheet'></link>\n"
				+ "<script type='text/javascript' src='js/jquery.js'></script>\n"
				
				+ "<script type='text/javascript'>\n"
				+ "$( document ).bind( 'mobileinit', function(){\n"
				+ "//$.mobile.ajaxLinksEnabled = false;\n"
				+ "  $.mobile.ajaxFormsEnabled = false;\n"
				+ "} );\n"
				+ "</script>\n"

				+ "<script type='text/javascript' src='js/jquery.mobile-1.0a4.1.min.js'></script>\n"

				+ "<link rel='stylesheet' href='js/dropdown.css' type='text/css'/>\n"
				+ "<script type='text/javascript' src='js/dropdown.js'></script>\n"
				
				+ "<link rel='stylesheet' href='js/jquery.treeview.css' />\n"
				+ "<script src='js/jquery.treeview.js' type='text/javascript'></script>\n"

				+ "<script type='text/javascript'>\n"
				+ "$( document ).ready( function(){\n"
				+ " $( '#" + view_unid + "' ).treeview();\n"
				+ "});\n"
				+ "</script>\n"
				
				+ "</head>\n"
				+ "<body>\n";
			
			String mddselect = "<dl class='dropdown'>\n"
					+ "<dt id='one-ddheader' onMouseOver='ddMenu( \"one\", 1 );' onMouseOut='ddMenu( \"one\", -1 );'>New</dt>\n"
					+ "<dd id='one-ddcontent' onMouseOver='cancelHide( \"one\" );' onMouseOut='ddMenu( \"one\", -1 );'>\n"
					+ "<ul>\n";
			String mselect = "<select name='formunid'>\n";
			for( Iterator it = formmap.entrySet().iterator(); it.hasNext(); ){
				Map.Entry entry = ( Map.Entry )it.next();
				String formid = ( String )entry.getKey();
				String[] formnames = ( String[] )entry.getValue();
				
				mselect += ( "<option value='" + formid + "'" );
				if( formid.equals( formunid ) ){
					mselect += ( " selected='selected'" );
				}
				mselect += ( ">" + formnames[0] + "</option>\n" );
				
				String ddRef = "/getdoc?repid=" + repid + "&amp;views=" + view_unid + "&amp;formunid=" + formid + "&amp;e=1&amp;m=1";
				String ddA = "<a href='" + ddRef + "' class='underline'>" + formnames[0] + "</a>";
				
				String ddLi = "<li>" + ddA + "</li>\n";
				mddselect += ddLi;
			}
			mselect += "</select>\n";
			mddselect += "</ul>\n</dd>\n</dl>\n";
			
			mview_xsl += "<div data-role='page' id='top'>\n"
				+ "<div data-role='header'>\n"
				
				+ "<a href='#' data-rel='back'>&lt;&lt;</a>\n" //. 「戻る」
				
				+ "<h1>" + view_name + "</h1>\n"
			
				+ "<a href='#dialog.htm' data-role='button' data-rel='dialog'>New</a>\n";
				
			mview_xsl += "</div>\n"
				+ "<div data-role='content'>\n";
/*				
				+ "<form method='get' action='/getdoc'>\n"
				+ "<div align='right'>\n"
				+ mselect + "<input type='submit' value='New'/>"
				+ "<input type='hidden' name='repid' value='" + repid + "'/>\n"
				+ "<input type='hidden' name='e' value='1'/>\n"
				+ "<input type='hidden' name='m' value='1'/>\n"
				+ "<input type='hidden' name='views' value='" + view_unid + "'/>\n"
				+ "</div>\n"
				+ "</form>\n";
*/	

			mview_xsl += "<xsl:apply-templates select='/database'/>\n";
				
			mview_xsl += "\n"
				+ "</div>\n"
				+ "<div data-role='footer'>\n"
				+ "</div>\n"
				+ "</div>\n"
				+ "</body>\n"
				+ "</html>\n"
				+ "</xsl:template>\n\n";

			mview_xsl += "<xsl:template match='/database'>\n"
				+ "<ul data-filter='true' id='" + view_unid + "' class='treeview'>\n"
				+ "<xsl:apply-templates select='document'/>\n"
				+ "</ul>\n"
				+ "</xsl:template>\n\n";
			
			mview_xsl += "<xsl:template name='doc' match='document'>\n";
			
			//. 各 document ごとのデータを <li> ～ </li> 内で指定
			String mviewLi = "<li>\n"
					+ "<a>\n"
					+ "<xsl:attribute name='href'>\n"
					+ "<xsl:value-of select='./noteinfo/@unid'/>.xml&amp;m=1"
					+ "</xsl:attribute>\n"
					+ "<xsl:attribute name='rel'>external</xsl:attribute>\n";
			for( int i = 0; i < columnNum; i ++ ){
				if( !cHidden[i].equals( "true" ) ){
					//. この列の内容として表示させるもの
					String colvalue = "";
					if( cItemName[i].startsWith( "$" ) ){
						String formula = cFormula[i].toLowerCase();
						System.out.println( "'formula = " + formula );
						if( formula.equals( "@created" ) ){
							colvalue = "<xsl:apply-templates select='./noteinfo/created/datetime'/>";
						}else if( formula.equals( "@modified" ) ){	
							colvalue = "<xsl:apply-templates select='./noteinfo/modified/datetime'/>";
						}else if( formula.equals( "@username" ) ){	
							colvalue = "<xsl:apply-templates select='./updatedby/name'/>";
						}else if( formula.equals( "@name([cn]; @author)" ) ){	
							colvalue = "<xsl:apply-templates select='./updatedby/name'/>";
						}else{
///							colvalue = "<xsl:copy-of select='./item[@name=\"" + cTitle[i] + "\"]'/>";
							colvalue = getXslFormulaValue( cTitle[i] );
						}
					}else{
///						colvalue = "<xsl:copy-of select='./item[@name=\"" + cItemName[i] + "\"]'/>";
						colvalue = getXslFormulaValue( cItemName[i] );
					}
					
					if( cCategorized[i].equals( "true" ) ){
						//. カテゴリ列 -> 展開可能にしたい
						
					}
					
					mviewLi += ( colvalue + "<xsl:text>  </xsl:text>\n" );
					
					//. スレッド
					if( cResOnly[i].equals( "true" ) ){
						i = columnNum;
					}
				}
			}
			mviewLi += "</a>\n";
			
			mviewLi += "<ul><xsl:for-each select='./document'><xsl:call-template name='doc'/></xsl:for-each></ul>\n"; //. 子 document を再帰的に
			mviewLi += "</li>\n";
			
			mview_xsl += mviewLi;
			
			mview_xsl += "\n"
				+ "</xsl:template>\n";
			
			mview_xsl += "\n<xsl:template match='datetime'>\n"
				+ "<xsl:value-of select='substring( ., 1, 4 )'/>"
				+ "/<xsl:value-of select='substring( ., 5, 2 )'/>"
				+ "/<xsl:value-of select='substring( ., 7, 2 )'/>"
				+ "<xsl:text> </xsl:text>"  //. 空白を強制的に挿入
				+ "<xsl:value-of select='substring( ., 10, 2 )'/>"
				+ ":<xsl:value-of select='substring( ., 12, 2 )'/>"
				+ ":<xsl:value-of select='substring( ., 14, 2 )'/>\n"
				+ "</xsl:template>\n";
			
			//. XSL 内のタグを調整			
			mview_xsl = "<?xml version='1.0' ?>\n"
					+ "<xsl:stylesheet version='1.0' xmlns:xsl='http://www.w3.org/1999/XSL/Transform'>\n"
					+ "<xsl:output method='html' encoding='UTF-8'/>\n"
					+ mview_xsl
					+ "</xsl:stylesheet>\n";

			//. XSL ファイルを保存
			String mfilename = repid + "/" + html_folder + "/m" + view_unid + ".xsl";
			writeFileUTF8( mfilename, mview_xsl );


/*			
			String viewTable = "<table>\n<tr>\n";
			@SuppressWarnings("unused")
			String viewUl = "<ul dojo='dojox.mobile.RoundRectList'>\n";
			int columnNum = -1;
			@SuppressWarnings("unused")
			String view_body = "";
			String[] cItemName = null, cTitle = null, cResOnly = null, cWidth = null, cFormula = null, cCategorized = null, cHidden = null;
			try{
				NodeList columnList = viewElement.getElementsByTagName( "column" );
				columnNum = columnList.getLength();
				cItemName = new String[columnNum];
				cTitle = new String[columnNum];
				cResOnly = new String[columnNum];
				cWidth = new String[columnNum];
				cFormula = new String[columnNum];
				cCategorized = new String[columnNum];
				cHidden = new String[columnNum];
				for( int i = 0; i < columnNum; i ++ ){
					Element columnElement = ( Element )columnList.item( i );
					cItemName[i] = columnElement.getAttribute( "itemname" );
					cResOnly[i] = columnElement.getAttribute( "responseonly" );
					cWidth[i] = columnElement.getAttribute( "width" );
					cTitle[i] = cFormula[i] = cCategorized[i] = cHidden[i] = "";

					try{
						cCategorized[i] = columnElement.getAttribute( "categorized" );
					}catch( Exception e ){
					}

					try{
						cHidden[i] = columnElement.getAttribute( "hidden" );
					}catch( Exception e ){
					}

					try{
						NodeList headerList = columnElement.getElementsByTagName( "columnheader" );
						Element headerElement = ( Element )headerList.item( 0 );
						cTitle[i] = headerElement.getAttribute( "title" );
					}catch( Exception e ){
					}
					if( cTitle[i] == null || cTitle[i].length() == 0 ){
						cTitle[i] = cItemName[i];
					}
					
					if( cItemName[i].startsWith( "$" ) ){
						try{
							NodeList codeList = columnElement.getElementsByTagName( "code" );
							Element codeElement = ( Element )codeList.item( 0 );
							NodeList formulaList = codeElement.getElementsByTagName( "formula" );
							Element formulaElement = ( Element )formulaList.item( 0 );
							cFormula[i] = formulaElement.getFirstChild().getNodeValue();
						}catch( Exception e ){
						}
					}
					
					//System.out.println( "[" + i + "] : itemName = " + cItemName[i] + ", resOnly = " + cResOnly[i] + ", width = " + cWidth[i] + ", title = " + cTitle[i] + ", formula = " + cFormula[i] );
					
					if( !cHidden[i].equals( "true" ) ){
						//. 実際の幅は指定値の10倍とする
						int width = ( int )Double.parseDouble( cWidth[i] ) * 10;
						
						String viewTh = "<th width='" + width + "'>" + cTitle[i] + "</th>\n";
						viewTable += viewTh;
	
						//. モバイル用ではこの部分は不要？
						String viewLi = "<li dojoType=\"dojox.mobile.ListItem\" transition=\"slide\" class=\"mblVariableHeight\" style=\"font-size:13px\">" + cTitle[i] + "</li>\n";
						viewUl += viewLi;
					}
				}
			}catch( Exception e ){
				e.printStackTrace();
			}
			viewTable += "</tr>\n";
			
			String view_xsl = "<xsl:template match='/'>\n"
				+ "<html>\n"
				+ "<head>\n"
				+ "<title>" + view_name + "</title>\n"
				+ "</head>\n"
				+ "<body";
			if( view_bgcolor != null && view_bgcolor.length() > 0 ){
				view_xsl += ( " bgcolor='" + view_bgcolor + "'" );
			}
			view_xsl += ">\n";
			
			view_xsl += "<h1>" + view_name + "</h1>\n";
			
			view_xsl += viewUl + "\n";
				
			view_xsl += "<xsl:apply-templates select='/database/document'/>\n";
				
			view_xsl += "\n"
				+ "</ul>\n"
				+ "</body>\n"
				+ "</html>\n"
				+ "</xsl:template>\n\n";

			view_xsl += "<xsl:template match='/database/document'>\n";

			view_xsl += "<li>\n";
			
			boolean isFirst = true;
			for( int i = 0; i < columnNum; i ++ ){
				if( !cHidden[i].equals( "true" ) ){
					String viewTd = "<li>";
					
					if( isFirst ){
//						viewTd += "<a target=\"_blank\" href=\"<xsl:value-of select='./noteinfo/@unid'/>.xml\">";
						viewTd += "<a>\n"
								+ "<xsl:attribute name='target'>preview</xsl:attribute>\n"
								+ "<xsl:attribute name='href'>\n"
								+ "<xsl:value-of select='./noteinfo/@unid'/>.xml"
								// &amp;xslid=(form_unid)
								+ "</xsl:attribute>\n";
					}
					
					if( cItemName[i].startsWith( "$" ) ){
						String formula = cFormula[i].toLowerCase();
						if( formula.equals( "@created" ) ){
//							viewTd += "<xsl:value-of select=\"format-dateTime( './noteinfo/created', '[Y]/[M01]/[D01] [H]:[M]:[S]' )\"/>\n";
							viewTd += "<xsl:copy-of select='./noteinfo/created'/>\n";
						}else if( formula.equals( "@modified" ) ){	
//							viewTd += "<xsl:value-of select='./noteinfo/modified'/>\n";
							viewTd += "<xsl:copy-of select='./noteinfo/modified'/>\n";
						}else if( formula.equals( "@username" ) ){	
//							viewTd += "<xsl:value-of select='./noteinfo/updatedby'/>\n";
							viewTd += "<xsl:copy-of select='./noteinfo/updatedby'/>\n";
						}else{
//							viewTd += "<xsl:value-of select='./item[@name=\"" + cTitle[i] + "\"]'/>\n";
							viewTd += "<xsl:copy-of select='./item[@name=\"" + cTitle[i] + "\"]'/>\n";
						}
					}else{
//						viewTd += "<xsl:value-of select='./item[@name=\"" + cItemName[i] + "\"]'/>\n"; //cItemName[i];
						viewTd += "<xsl:copy-of select='./item[@name=\"" + cItemName[i] + "\"]'/>\n"; //cItemName[i];
					}

					if( isFirst ){
						viewTd += "</a>\n";
					}

					viewTd += "</td>";

					view_xsl += viewTd;
					
					isFirst = false;
				}
			}
			
			view_xsl += "</tr>\n"
				+ "</xsl:template>\n";
			
			//. XSL 内のタグを調整			
			view_xsl = "<?xml version='1.0' ?>\n"
					+ "<xsl:stylesheet version='1.0' xmlns:xsl='http://www.w3.org/1999/XSL/Transform'>\n"
					+ "<xsl:output method='html' encoding='UTF-8'/>\n"
					+ view_xsl
					+ "</xsl:stylesheet>\n";

			//. XSL ファイルを保存
			String filename = repid + "/" + html_folder + "/" + view_unid + ".xsl";
			writeFileUTF8( filename, view_xsl );

			
			
			//. モバイル用 XSL
			String view_xsl_m = "<xsl:template match='/'>\n"
				+ "<html>\n"
				+ "<head>\n"
				+ "<title>" + view_name + "</title>\n"

				+ "<meta name='viewport' content='width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no'/>\n"
				+ "<meta name='apple-mobile-web-app-capable' content='yes' /> \n"
				+ "<script type='text/javascript'>\n"
				+ "<!--\n"
				+ "  window.onload = function(){\n"
				+ "    setTimeout( scrollTo, 100, 0, 1 );\n"
				+ "  }\n"
				+ "// -->\n"
				+ "</script>\n"
				+ "<link href='js/dojox/mobile/themes/" + mobile_css + "/" + mobile_css + ".css' rel='stylesheet'></link>\n"
				+ "<script type='text/javascript' src='js/dojo/dojo.js' djConfig='parseOnLoad: true'></script>\n"
				+ "<script language='JavaScript' type='text/javascript'>\n"
				+ "<!--\n"
				+ "dojo.require( 'dojox.mobile.parser' );\n"
				+ "dojo.require( 'dojox.mobile' );\n"
				+ "dojo.require( 'dojox.mobile.compat' );\n"
				+ "// -->\n"
				+ "</script>\n"

				+ "</head>\n"
				+ "<body>\n"
				
				+ "<div id='settings' dojoType='dojox.mobile.View' selected='true'>\n"

				+ "<h1 align='middle' icon='/get?id=" + repid + "-" + picture_folder + "/" + sys_imgs_dbicon + "' dojoType='dojox.mobile.Heading' back='Back' href='javascript:history.back();'>" + view_name + "</h1>\n"
				+ "<ul dojoType='dojox.mobile.RoundRectList'>";

			view_xsl_m += ( isAll ) ?
					"<xsl:apply-templates select='/database/document'/>\n" 
					: "<xsl:apply-templates select='/database/document[@form=\"" + select_form + "\"]'/>\n";
				
			view_xsl_m += "\n"
				+ "</ul>\n"
				+ "</div>\n"
				+ "</body>\n"
				+ "</html>\n"
				+ "</xsl:template>\n\n";

			view_xsl_m += ( isAll ) ?
					"<xsl:template match='/database/document'>\n" 
					: "<xsl:template match='/database/document[@form=\"" + select_form + "\"]'>\n";

			isFirst = true;
			String viewLi = "<li dojoType='dojox.mobile.ListItem' transition='slide' class='mblVariableHeight' style='font-size:13px'>";
			for( int i = 0; i < columnNum; i ++ ){
				if( !cHidden[i].equals( "true" ) ){					
					if( isFirst ){
//						viewTd += "<a target=\"_blank\" href=\"<xsl:value-of select='./noteinfo/@unid'/>.xml\">";
						viewLi += "<a>\n"
								+ "<xsl:attribute name='href'>\n"
								+ "<xsl:value-of select='./noteinfo/@unid'/>.xml&amp;m=1\n"
								+ "</xsl:attribute>\n";
					}
					
					if( cItemName[i].startsWith( "$" ) ){
						String formula = cFormula[i].toLowerCase();
						if( formula.equals( "@created" ) ){
//							viewLi += "<xsl:value-of select='./noteinfo/created'/>\n";
							viewLi += "<xsl:copy-of select='./noteinfo/created'/>\n";
						}else if( formula.equals( "@modified" ) ){	
//							viewLi += "<xsl:value-of select='./noteinfo/modified'/>\n";
							viewLi += "<xsl:copy-of select='./noteinfo/modified'/>\n";
						}else if( formula.equals( "@username" ) ){	
//							viewLi += "<xsl:value-of select='./noteinfo/updatedby'/>\n";
							viewLi += "<xsl:copy-of select='./noteinfo/updatedby'/>\n";
						}else{
//							viewLi += "<xsl:value-of select='./item[@name=\"" + cTitle[i] + "\"]'/>\n";
							viewLi += "<xsl:copy-of select='./item[@name=\"" + cTitle[i] + "\"]'/>\n";
						}
					}else{
//						viewLi += "<xsl:value-of select='./item[@name=\"" + cItemName[i] + "\"]'/>\n"; //cItemName[i];
						viewLi += "<xsl:copy-of select='./item[@name=\"" + cItemName[i] + "\"]'/>\n"; //cItemName[i];
					}

					if( isFirst ){
						viewLi += "</a>\n";
					}
					
					isFirst = false;
				}
			}
			viewLi += "</li>\n";

			view_xsl_m += viewLi;
			
			view_xsl_m += "</xsl:template>\n";
			
			//. XSL 内のタグを調整			
			view_xsl_m = "<?xml version='1.0' ?>\n"
					+ "<xsl:stylesheet version='1.0' xmlns:xsl='http://www.w3.org/1999/XSL/Transform'>\n"
					+ "<xsl:output method='html' encoding='UTF-8'/>\n"
					+ view_xsl_m
					+ "</xsl:stylesheet>\n";

			//. XSL ファイルを保存			
			String mfilename = repid + "/" + html_folder + "/m" + view_unid + ".xsl";
			writeFileUTF8( mfilename, view_xsl_m );
*/

		}catch( Exception e ){
			e.printStackTrace();
		}
	}
	
	private static String getXslFormulaValue( String formula ){
		String value = "";
		
//		System.out.println( "formula = " + formula );
///		value = "<xsl:copy-of select='./item[@name=\"" + formula + "\"]'/>";
		String[] stmp1 = formula.split( ";" );
		if( stmp1.length == 1 ){
			value += ( "<xsl:copy-of select='./item[@name=\"" + formula + "\"]'/>" );
		}else{
			for( int i = 0; i < stmp1.length; i ++ ){
//				System.out.println( " stmp1[" + i + "] = " + stmp1[i] );
				String[] stmp2 = stmp1[i].split( "+" );
				for( int j = 0; j < stmp2.length; j ++ ){
					System.out.println( "  stmp2[" + j + "] = " + stmp1[j] );
					String v = stmp2[j].trim();
					if( v != null && v.length() > 0 && !v.startsWith( "@" ) ){
						value += ( "<xsl:copy-of select='./item[@name=\"" + v + "\"]'/>" );
					}
				}
			}
		}
		
		return value;
	}

	private static String getValue( Object obj, int tdfmt ){
		String value = "";
		String classname = obj.getClass().getName();

		try{
			if( classname.equals( "lotus.domino.local.DateTime" ) ){
				if( tdfmt == ViewColumn.FMT_DATE ){
					value = ( ( lotus.domino.local.DateTime )obj ).getDateOnly();
				}else if( tdfmt == ViewColumn.FMT_TIME ){
					value = ( ( lotus.domino.local.DateTime )obj ).getTimeOnly();
				}else{
					value = ( ( lotus.domino.local.DateTime )obj ).getDateOnly() + " " + ( ( lotus.domino.local.DateTime )obj ).getTimeOnly();
				}
			}else if( classname.equals( "java.lang.Double" ) ){
				value = "" + obj;
			}else if( classname.equals( "java.lang.Float" ) ){
				value = "" + obj;
			}else{
				value = ( String )obj;
			}
			
			if( value == null ) value = "";
			if( value.length() == 0 ) value = "(no value)";
		}catch( Exception e ){
			e.printStackTrace();
		}
		
		return value;		
	}
	
	@SuppressWarnings("unused")
	private static int getAclLevel( String name ){
		int r = 0;
		if( name.equals( "manager" ) ){
			r = 6;
		}else if( name.equals( "designer" ) ){
			r = 5;
		}else if( name.equals( "editor" ) ){
			r = 4;
		}else if( name.equals( "author" ) ){
			r = 3;
		}else if( name.equals( "reader" ) ){
			r = 2;
		}else if( name.equals( "depositor" ) ){
			r = 1;
		}

		return r;
	}
	
	@SuppressWarnings({ "unused", "unchecked" })
	private static String[] getValues( Object obj, int tdfmt ){
		String[] values = null;
		String classname = obj.getClass().getName();
		
		try{
			if( classname.equals( "java.util.Vector" ) ){
				int n = ( ( Vector )obj ).size();
				values = new String[n];
				for( int i = 0; i < n; i ++ ){
					values[i] = getValue( ( ( Vector )obj ).get( i ), tdfmt );
				}
			}else{
				values = new String[1];
				values[0] = getValue( obj, tdfmt );
			}
		}catch( Exception e ){
			e.printStackTrace();
		}

		return values;
	}
	
	public static boolean binCopy( String srcfilename, String dstfilename ){
		boolean b = false;
		
		//. バイナリファイルのコピー
		try{
			FileInputStream fis = new FileInputStream( srcfilename );
			FileOutputStream fos = new FileOutputStream( dstfilename );
			byte buf[] = new byte[1024];
			int len;
			while( ( len = fis.read( buf ) ) != -1 ){
				fos.write( buf, 0, len );
			}
			
			fos.flush();
			fos.close();
			fis.close();
		}catch( Exception e ){
			e.printStackTrace();
		}
		
		return b;
	}

	private static byte[] B64decode( String b64enc ){
		int fillchar = '=';
		String cvt = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
				+ "abcdefghijklmnopqrstuvwxyz"
				+ "0123456789+/";
		byte[] b = null;
		
		try{
			b64enc = b64enc.replaceAll( "\r\n", "" );
			b64enc = b64enc.replaceAll( "\n", "" );
			byte[] src = b64enc.getBytes();
			int len = src.length;
			int r = ( len * 3 ) / 4;
			b = new byte[r];
			
			for( int i = 0, j = 0; i < len; i ++ ){
				int c = cvt.indexOf( src[i++] );
				int c1 = cvt.indexOf( src[i] );
				c = ( ( c << 2 ) | ( ( c1 >> 4 ) & 0x3 ) );
				Integer it = new Integer( ( int )c );
				b[j++] = it.byteValue();
				if( ++i < len ){
					c = src[i];
					if( fillchar == c ){
						break;
					}
					
					c = cvt.indexOf( ( byte )c );
					c1 = ( ( c1 << 4 ) & 0xf0 ) | ( ( c >> 2 ) & 0xf );
					it = new Integer( ( int )c1 );
					b[j++] = it.byteValue();
				}
				
				if( ++i < len ){
					c1 = src[i];
					if( fillchar == c1 ){
						break;
					}
					
					c1 = cvt.indexOf( ( byte )c1 );
					c = ( ( c << 6 ) & 0xc0 ) | c1;
					it = new Integer( ( int )c );
					b[j++] = it.byteValue();
				}
			}
		}catch( Exception e ){
			e.printStackTrace();
		}
		
		return b;
	}
	

	private static String createRequestCode( String res, String http_request_verb, String content_md5, String content_type, String[] xgoog ){
		String r = http_request_verb + "\n"
				+ content_md5 + "\n"
				+ content_type + "\n"
				+ createDateString() + "\n";
		
		if( xgoog != null ){
			for( int i = 0; i < xgoog.length; i ++ ){
				String xg = xgoog[i].toLowerCase();
				String[] xgs = xg.split( ": " );
				if( xgs.length >= 2 ){
					xg = xgs[0] + ":" + xgs[1];
				}
				r += ( xg + "\n" );
			}
		}
		
		r += ( res );
		
		return r;
	}
	
	private static String createDateString(){
		String[] month = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
		String[] week = { "Sun", "Mon", "Tue", "Wed", "Thr", "Fri", "Sat" };
		Calendar c = Calendar.getInstance();
		int y = c.get( Calendar.YEAR );
		int m = c.get( Calendar.MONTH );
		int d = c.get( Calendar.DAY_OF_MONTH );
		int h = c.get( Calendar.HOUR_OF_DAY );
		int n = c.get( Calendar.MINUTE );
		int s = c.get( Calendar.SECOND );
		int z = c.get( Calendar.ZONE_OFFSET );
		int w = c.get( Calendar.DAY_OF_WEEK ) - 1;
		z = z / 36000;
		
		String r = week[w] + ", " + ( ( d < 10 ) ? "0" : "" ) + d + " " + month[m] + " " + y + " "
				+ ( ( h < 10 ) ? "0" : "" ) + h + ":"
				+ ( ( n < 10 ) ? "0" : "" ) + n + ":"
				+ ( ( s < 10 ) ? "0" : "" ) + s + " "
				+ ( ( z == 0 ) ? "GMT" : ( ( z > 0 ) ? "+" : "-" ) + ( ( z <= -1000 || z >= 1000 ) ? "" : "0" ) + ( ( z < 0 ) ? ( -1 * z ) : z ) );
		
		return r;
	}


	public static void DbgPrint( String msg ){
		if( dbg ){
			System.out.print( msg );
		}
	}

	public static void DbgPrintln( String msg ){
		if( dbg ){
			System.out.println( msg );
		}
	}
}

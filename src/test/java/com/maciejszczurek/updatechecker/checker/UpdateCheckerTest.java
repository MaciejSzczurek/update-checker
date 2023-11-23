package com.maciejszczurek.updatechecker.checker;

import static com.maciejszczurek.updatechecker.chrome.service.ChromeServiceHolderTest.CHROME_DRIVER_VERSION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.maciejszczurek.updatechecker.application.NewVersionNotFoundException;
import com.maciejszczurek.updatechecker.checker.annotation.ApplicationType;
import com.maciejszczurek.updatechecker.chrome.service.ChromeDriverHolder;
import com.maciejszczurek.updatechecker.cookie.service.CookieHolder;
import com.maciejszczurek.updatechecker.option.service.OptionService;
import com.maciejszczurek.updatechecker.service.UserAgents;
import java.io.IOException;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.URI;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.function.Consumer;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.transaction.PlatformTransactionManager;

@NoArgsConstructor
public class UpdateCheckerTest {

  private static final Logger log = LogManager.getLogger(
    UpdateCheckerTest.class
  );

  private static final CookieManager cookieManager = new CookieManager();
  private static ChromeDriverHolder chromeService;
  private static CookieHolder cookieHolder;

  private static void checkUpdate(@NotNull final UpdateChecker updateChecker)
    throws IOException, InterruptedException {
    updateChecker.checkUpdate();
    assertThat(updateChecker.isUpdate()).isTrue();

    updateChecker.update();
    assertThat(updateChecker.getCurrentVersion())
      .isEqualTo(updateChecker.getNewVersion());
    assertThat(updateChecker.getNewVersion())
      .doesNotStartWith(" ")
      .doesNotEndWith(" ");

    log.debug(
      "{}: {}",
      Objects
        .requireNonNull(
          AnnotationUtils.findAnnotation(
            updateChecker.getClass(),
            ApplicationType.class
          )
        )
        .value(),
      updateChecker.getCurrentVersion()
    );
  }

  @BeforeAll
  public static void beforeClass() throws Exception {
    UserAgents.generateUserAgent();

    final var optionService = mock(OptionService.class);
    final var resourceLoader = new DefaultResourceLoader();

    when(optionService.getOption("chrome-driver-version", ""))
      .thenReturn(CHROME_DRIVER_VERSION);

    chromeService = new ChromeDriverHolder(optionService, resourceLoader);
    chromeService.initialize();

    cookieHolder =
      new CookieHolder(
        chromeService,
        cookieManager,
        mock(PlatformTransactionManager.class)
      );
  }

  @AfterAll
  public static void afterClass() {
    chromeService.quit();
  }

  @Test
  void dobreprogramy() throws IOException, InterruptedException {
    checkUpdate(
      new DobreprogramyUpdateChecker(
        "https://www.dobreprogramy.pl/ABBYY-FineReader-Corporate,Program,Windows,12711.html",
        ""
      )
    );
  }

  @Test
  void osmandMaps() throws IOException, InterruptedException {
    checkUpdate(
      new OsmandMapsUpdateChecker("https://download.osmand.net/list.php", "")
    );
  }

  @Test
  void mvnRepo() throws IOException, InterruptedException {
    checkUpdate(
      new MvnRepoUpdateChecker(
        "https://mvnrepository.com/artifact/commons-io/commons-io",
        ""
      )
    );
    checkUpdate(
      new MvnRepoUpdateChecker(
        "https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-databind",
        ""
      )
    );
    checkUpdate(
      new MvnRepoUpdateChecker(
        "https://mvnrepository.com/artifact/jakarta.validation/jakarta.validation-api/",
        ""
      )
    );
  }

  @Test
  void composer() throws IOException, InterruptedException {
    checkUpdate(
      new ComposerUpdateChecker("https://getcomposer.org/download", "")
    );
  }

  @Test
  void npmRepo() throws IOException, InterruptedException {
    checkUpdate(
      new NpmRepoUpdateChecker("https://www.npmjs.com/package/react-dom", "")
    );
    checkUpdate(
      new NpmRepoUpdateChecker(
        "https://www.npmjs.com/package/@coreui/coreui",
        ""
      )
    );
    checkUpdate(
      new NpmRepoUpdateChecker(
        "https://www.npmjs.com/package/@coreui/coreui/v/v2-lts",
        ""
      )
    );
    checkUpdate(
      new NpmRepoUpdateChecker(
        "https://www.npmjs.com/package/webpack/v/webpack-4",
        ""
      )
    );
  }

  @Test
  void httpApacheListLastModified() throws IOException, InterruptedException {
    checkUpdate(
      new HttpApacheListLastModifiedUpdateChecker(
        "https://docs.spring.io/spring-boot/docs/current/reference/pdf",
        ""
      )
    );

    checkUpdate(
      new HttpApacheListLastModifiedUpdateChecker(
        "https://docs.spring.io/spring/docs/current/spring-framework-reference/pdf/?C=M;O=D",
        ""
      )
    );

    checkUpdate(
      new HttpApacheListLastModifiedUpdateChecker(
        "http://mirror.centos.org/centos/7/updates/x86_64/Packages/?C=M;O=D",
        ""
      )
    );
  }

  @Test
  void packagist() throws IOException, InterruptedException {
    checkUpdate(
      new PackagistUpdateChecker(
        "https://packagist.org/packages/symfony/framework-standard-edition",
        ""
      )
    );
  }

  @Test
  void ccEnhancer() throws IOException, InterruptedException {
    checkUpdate(
      new CCEnhancerUpdateChecker(
        "https://singularlabs.com/software/ccenhancer/download-ccenhancer/",
        ""
      )
    );
  }

  @Test
  void winapp2() throws IOException, InterruptedException {
    checkUpdate(
      new Winapp2UpdateChecker(
        "http://content.thewebatom.net/files/winapp2.ini",
        ""
      )
    );
  }

  @Test
  void ccleaner() throws IOException, InterruptedException {
    checkUpdate(
      new CCleanerUpdateChecker(
        "https://www.ccleaner.com/pl-pl/ccleaner/version-history",
        ""
      )
    );
  }

  @Test
  void cygwin() throws IOException, InterruptedException {
    checkUpdate(new CygwinUpdateChecker("https://cygwin.com/index.html", ""));
  }

  @Test
  void cygwinSetup() throws IOException, InterruptedException {
    checkUpdate(
      new CygwinSetupUpdateChecker(
        "http://cygwin.mirrors.hoobly.com/x86_64/setup.ini",
        ""
      )
    );
  }

  @Test
  void driverGenius() throws IOException, InterruptedException {
    checkUpdate(
      new DriverGeniusUpdateChecker(
        "http://www.driver-soft.com/download.html",
        ""
      )
    );
  }

  @Test
  void gitScm() throws IOException, InterruptedException {
    checkUpdate(
      new GitScmUpdateChecker("https://git-scm.com/download/win", "")
    );
  }

  @Test
  void gpg4win() throws IOException, InterruptedException {
    checkUpdate(
      new Gpg4winUpdateChecker(
        "https://www.gpg4win.org/thanks-for-download.html",
        ""
      )
    );
  }

  @Test
  void jetBrains() throws IOException, InterruptedException {
    checkUpdate(
      new JetBrainsUpdateChecker(
        "https://data.services.jetbrains.com/products/releases?code=IIU%2CIIC&latest=true&type=release",
        ""
      )
    );
  }

  @Test
  void jetBrainsWithBuild() throws IOException, InterruptedException {
    checkUpdate(
      new JetBrainsWithBuildUpdateChecker(
        "https://data.services.jetbrains.com/products/releases?code=YTD%2CYTWE&latest=true&type=release",
        ""
      )
    );
    checkUpdate(
      new JetBrainsWithBuildUpdateChecker(
        "https://data.services.jetbrains.com/products/releases?code=HB&latest=true&type=release",
        ""
      )
    );
  }

  @Test
  void javaSceneBuilder() throws IOException, InterruptedException {
    checkUpdate(
      new JavaSceneBuilderUpdateChecker(
        "http://gluonhq.com/products/scene-builder/",
        ""
      )
    );
  }

  @Test
  void josm() throws IOException, InterruptedException {
    final var updateChecker = new JosmUpdateChecker(
      "https://josm.openstreetmap.de/wiki/Pl%3AWikiStart",
      ""
    );
    updateChecker.setChromeDriverHolder(chromeService);
    checkUpdate(updateChecker);
  }

  @Test
  void macPass() throws IOException, InterruptedException {
    checkUpdate(new MacPassUpdateChecker("https://macpass.github.io/", ""));
  }

  @Test
  void navicat() throws IOException, InterruptedException {
    checkUpdate(
      new NavicatUpdateChecker(
        "https://www.navicat.com/en/products/navicat-premium-release-note",
        ""
      )
    );
  }

  @Test
  void nodejs() throws IOException, InterruptedException {
    checkUpdate(new NodejsUpdateChecker("https://nodejs.org/en", ""));
  }

  @Test
  void notepadPP() throws IOException, InterruptedException {
    checkUpdate(
      new NotepadPPUpdateChecker("https://notepad-plus-plus.org/downloads", "")
    );
  }

  @Test
  void regexBuddy() throws IOException, InterruptedException {
    checkUpdate(
      new RegexBuddyUpdateChecker("https://www.regexbuddy.com/history.html", "")
    );
  }

  @Test
  void tcUp() throws IOException, InterruptedException {
    checkUpdate(new TcUpUpdateChecker("https://www.tcup.pl/pobierz", ""));
  }

  @Test
  void tortoiseGit() throws IOException, InterruptedException {
    checkUpdate(
      new TortoiseGitUpdateChecker("https://tortoisegit.org/download", "")
    );
  }

  @Test
  void windowsRepair() throws IOException, InterruptedException {
    checkUpdate(
      new WindowsRepairUpdateChecker(
        "http://www.tweaking.com/content/page/windows_repair_all_in_one.html",
        ""
      )
    );
  }

  @Test
  void vlc() throws IOException, InterruptedException {
    checkUpdate(new VlcUpdateChecker("https://www.videolan.org/", ""));
  }

  @Test
  void navigationMaps() throws IOException, InterruptedException {
    final var updateChecker = new NavigationMapsUpdateChecker(
      "https://app-connect.volkswagen.com/mapupdates/car/",
      ""
    );
    updateChecker.setChromeDriverHolder(chromeService);
    checkUpdate(updateChecker);
  }

  @Test
  void fileHippo() throws IOException, InterruptedException {
    checkUpdate(
      new FileHippoUpdateChecker(
        "https://filehippo.com/pl/mac/download_winzip_mac_edition/",
        ""
      )
    );
  }

  @Test
  void xnView() throws IOException, InterruptedException {
    checkUpdate(
      new XnViewUpdateChecker("https://www.xnview.com/en/xnviewmp/", "")
    );
  }

  @Test
  void stationDrivers() throws IOException, InterruptedException {
    final var updateChecker = new StationDriversUpdateChecker(
      "https://www.station-drivers.com/index.php?option=com_remository&Itemid=353&func=select" +
      "&id=406&orderby=4&lang=en",
      ""
    );
    updateChecker.setName("Intel Chipset Device Software");
    checkUpdate(updateChecker);

    var badSortingUpdateChecker = new StationDriversUpdateChecker(
      "https://www.station-drivers.com/index.php/en/component/remository/Drivers/Intel" +
      "/Management-Engine-Interface-(MEI)/Drivers/11.x/MEI-1.5Mo/lang,en-gb/",
      ""
    );
    badSortingUpdateChecker.setNewVersion(
      "Intel Management Engine Interface (MEI)"
    );
    assertThatCode(badSortingUpdateChecker::checkUpdate)
      .isInstanceOf(NewVersionNotFoundException.class);

    badSortingUpdateChecker =
      new StationDriversUpdateChecker(
        "https://www.station-drivers.com/index.php?option=com_remository&Itemid=353&func=select&id=406&lang=en",
        ""
      );
    badSortingUpdateChecker.setNewVersion("Intel Chipset Device Software");
    assertThatCode(badSortingUpdateChecker::checkUpdate)
      .isInstanceOf(NewVersionNotFoundException.class);
  }

  @Test
  void amdDrivers() throws IOException, InterruptedException {
    checkUpdate(
      new AMDDriverUpdateChecker(
        "https://support.amd.com/en-us/download/desktop?os=Windows+10+-+64",
        ""
      )
    );
  }

  @Test
  void realtekEthernetWindows10() throws IOException, InterruptedException {
    checkUpdate(
      new RealtekEthernetWindows10UpdateChecker(
        "http://218.210.127.131/downloads/downloadsView.aspx?Langid=1&PNid=13&PFid=5&Level=5&" +
        "Conn=4&DownTypeID=3&GetDown=false",
        ""
      )
    );
  }

  @Test
  void baseWinWindows10() throws IOException, InterruptedException {
    checkUpdate(
      new BaseWinWindows10UpdateChecker(
        System.getProperty("tests.base-win.site-url"),
        ""
      )
    );
  }

  @Test
  void invision() throws IOException, InterruptedException {
    checkUpdate(
      new InvisionUpdateChecker(
        "http://www.i-n-v-i-s-i-o-n.com/forum/viewforum.php?f=1",
        ""
      )
    );
  }

  @Test
  void ccleanerMac() throws IOException, InterruptedException {
    checkUpdate(
      new CCleanerMacUpdateChecker(
        "https://www.ccleaner.com/ccleaner/download?mac",
        ""
      )
    );
  }

  @Test
  void smartVersion() throws IOException, InterruptedException {
    checkUpdate(
      new SmartVersionUpdateChecker(
        "http://www.smartversion.com/download.htm",
        ""
      )
    );
  }

  @Test
  void mariaDb() throws IOException, InterruptedException {
    checkUpdate(new MariaDbUpdateChecker("https://downloads.mariadb.org/", ""));
  }

  @Test
  void lombokEdge() throws IOException, InterruptedException {
    checkUpdate(
      new LombokEdgeUpdateChecker("https://projectlombok.org/download-edge", "")
    );
  }

  @Test
  void adobeDigitalEditions() throws IOException, InterruptedException {
    checkUpdate(
      new AdobeDigitalEditionsUpdateChecker(
        "https://www.adobe.com/pl/solutions/ebook/digital-editions/download.html",
        ""
      )
    );
  }

  @Test
  void winRarPl() throws IOException, InterruptedException {
    checkUpdate(
      new WinRarPlUpdateChecker("https://rarlab.com/download.htm", "")
    );
  }

  @Test
  void nvidiaDriver() throws IOException, InterruptedException {
    checkUpdate(
      new NvidiaDriverUpdateChecker(
        "https://www.nvidia.pl/Download/processDriver.aspx?psid=101&pfid=815&rpf=1&osid=57&lid=14&lang=pl&ctk=0&dtid=1&dtcid=1",
        ""
      )
    );
  }

  @Test
  void php() throws IOException, InterruptedException {
    checkUpdate(
      new PhpUpdateChecker("https://secure.php.net/downloads.php", "")
    );
  }

  @Test
  void chromiumWindows() throws IOException, InterruptedException {
    checkUpdate(
      new ChromiumWindowsUpdateChecker(
        "https://chromium.woolyss.com/api/v3/?os=windows&out=json&type=stable-codecs-sync",
        ""
      )
    );
  }

  @Test
  void chromiumMac() throws IOException, InterruptedException {
    checkUpdate(
      new ChromiumMacUpdateChecker("https://chromium.woolyss.com", "")
    );
  }

  @Test
  void officeRTool() throws IOException, InterruptedException {
    final var userCookie = new HttpCookie(
      "xf_user",
      System.getProperty("tests.office-r-tool.user-token")
    );
    userCookie.setVersion(0);
    userCookie.setPath("/");
    userCookie.setMaxAge(
      ZonedDateTime
        .now()
        .until(ZonedDateTime.now().plusMonths(1), ChronoUnit.SECONDS)
    );
    userCookie.setHttpOnly(true);
    userCookie.setSecure(true);
    cookieManager
      .getCookieStore()
      .add(
        URI.create(System.getProperty("tests.office-r-tool.effective-uri")),
        userCookie
      );

    final var officeRToolUpdateChecker = new OfficeRToolUpdateChecker(
      System.getProperty("tests.office-r-tool.site-url"),
      ""
    );
    officeRToolUpdateChecker.setCookieManager(cookieManager);
    checkUpdate(officeRToolUpdateChecker);
  }

  @Test
  void springBoot() throws IOException, InterruptedException {
    checkUpdate(new SpringBootUpdateChecker("https://start.spring.io/", ""));
  }

  @Test
  void camtasia() throws IOException, InterruptedException {
    checkUpdate(
      new CamtasiaUpdateChecker(
        "https://support.techsmith.com/hc/en-us/articles/115006443267%C2%A0",
        ""
      )
    );
  }

  @Test
  void putty() throws IOException, InterruptedException {
    checkUpdate(
      new PuttyUpdateChecker(
        "https://www.chiark.greenend.org.uk/~sgtatham/putty/latest.html",
        ""
      )
    );
  }

  @Test
  void pip() throws IOException, InterruptedException {
    final PipUpdateChecker pip1 = new PipUpdateChecker(
      "https://pypi.org/project/pip/",
      ""
    );
    final PipUpdateChecker pip2 = new PipUpdateChecker(
      "https://pypi.org/project/pip",
      ""
    );
    checkUpdate(pip1);
    checkUpdate(pip2);

    assertThat(pip1.getNewVersion()).isEqualTo(pip2.getNewVersion());
  }

  @Test
  void adoptium() throws IOException, InterruptedException {
    var adoptiumUpdateChecker = new AdoptiumUpdateChecker(
      "https://adoptium.net/releases.html",
      ""
    );
    adoptiumUpdateChecker.setName("Adoptium");
    checkUpdate(adoptiumUpdateChecker);
    adoptiumUpdateChecker =
      new AdoptiumUpdateChecker("https://adoptium.net/releases.html", "");
    adoptiumUpdateChecker.setName("Adoptium Windows");
    checkUpdate(adoptiumUpdateChecker);
  }

  @Test
  void gradlePlugin() throws IOException, InterruptedException {
    checkUpdate(
      new GradlePluginUpdateChecker(
        "https://plugins.gradle.org/plugin/com.github.spotbugs",
        ""
      )
    );
  }

  @Test
  void githubFile() throws IOException, InterruptedException {
    checkUpdate(
      new GithubFileUpdateChecker(
        "https://github.com/mailcow/mailcow-dockerized/blob/master/docker-compose.yml",
        ""
      )
    );
    checkUpdate(
      new GithubFileUpdateChecker(
        "https://github.com/docker-library/wordpress/blob/master/latest/php7.4/fpm-alpine/Dockerfile",
        ""
      )
    );
  }

  @Test
  void responseBody() throws IOException, InterruptedException {
    checkUpdate(
      new ResponseBodyUpdateChecker(
        "https://www.servercow.de/docker-compose/latest.php",
        ""
      )
    );
  }

  @Test
  void ubuntu() throws IOException, InterruptedException {
    checkUpdate(
      new UbuntuUpdateChecker(
        "https://www.ubuntuupdates.org/package/core/bionic/main/updates/linux",
        ""
      )
    );
  }

  @Test
  void python() throws IOException, InterruptedException {
    checkUpdate(
      new PythonUpdateChecker("https://www.python.org/downloads/source/", "")
    );
  }

  @Test
  void nano() throws IOException, InterruptedException {
    checkUpdate(
      new NanoUpdateChecker("https://www.nano-editor.org/download.php", "")
    );
  }

  @Test
  void zsh() throws IOException, InterruptedException {
    checkUpdate(
      new ZshUpdateChecker("http://zsh.sourceforge.net/Arc/source.html", "")
    );
  }

  @Test
  void softwareOk() throws IOException, InterruptedException {
    checkUpdate(
      new SoftwareOkUpdateChecker(
        "http://www.softwareok.com/?seite=Microsoft/AutoHideDesktopIcons",
        ""
      )
    );
  }

  @Test
  void fuseMacos() throws IOException, InterruptedException {
    checkUpdate(new FuseMacosUpdateChecker("https://osxfuse.github.io", ""));
  }

  @Test
  void s3browser() throws IOException, InterruptedException {
    checkUpdate(
      new S3BrowserUpdateChecker("https://s3browser.com/download.aspx", "")
    );
  }

  @Test
  void miRom() throws IOException, InterruptedException {
    final var updateChecker = new MiRomUpdateChecker(
      "https://new.c.mi.com/global/miuidownload/detail/device/1700361",
      ""
    );
    updateChecker.setName("Mi 9 SE EEA");
    checkUpdate(updateChecker);
  }

  @Test
  void freshTomato() throws IOException, InterruptedException {
    checkUpdate(new FreshTomatoUpdateChecker("https://freshtomato.org/", ""));
  }

  @Test
  void foxitReader() throws IOException, InterruptedException {
    checkUpdate(
      new FoxitReaderUpdateChecker(
        "https://www.foxitsoftware.com/portal/download/getdownloadform?retJson=1&product=Foxit-Reader&platform=Mac-OS-X&formId=download-reader",
        ""
      )
    );
  }

  @Test
  void java() throws IOException, InterruptedException {
    final var javaUpdateChecker = new JavaUpdateChecker(
      "https://www.oracle.com/java/technologies/downloads/",
      ""
    );
    final var java17UpdateChecker = new JavaUpdateChecker(
      "https://www.oracle.com/java/technologies/downloads/#java17",
      ""
    );
    final var java11UpdateChecker = new JavaUpdateChecker(
      "https://www.oracle.com/java/technologies/downloads/#java11",
      ""
    );
    final var java8UpdateChecker = new JavaUpdateChecker(
      "https://www.oracle.com/java/technologies/downloads/#java8",
      ""
    );

    checkUpdate(javaUpdateChecker);
    checkUpdate(java17UpdateChecker);
    checkUpdate(java11UpdateChecker);
    checkUpdate(java8UpdateChecker);
    assertThat(java17UpdateChecker.getNewVersion()).startsWith("17");
    assertThat(java11UpdateChecker.getNewVersion()).startsWith("11");
    assertThat(java8UpdateChecker.getNewVersion()).startsWith("8");
  }

  @Test
  void urnyxGcam() throws IOException, InterruptedException {
    checkUpdate(
      new GcamUpdateChecker(
        "https://www.celsoazevedo.com/files/android/google-camera/dev-bsg/f/dl87/",
        ""
      )
    );
  }

  @Test
  void mysql() throws IOException, InterruptedException {
    checkUpdate(
      new MySQLUpdateChecker("https://dev.mysql.com/downloads/installer/", "")
    );
  }

  @Test
  void dockerHub() throws IOException, InterruptedException {
    checkUpdate(
      new DockerHubUpdateChecker(
        "https://hub.docker.com/_/php?tab=tags&name=fpm-alpine",
        ""
      )
    );
    checkUpdate(
      new DockerHubUpdateChecker(
        "https://hub.docker.com/r/mcuadros/ofelia/tags?name=latest",
        ""
      )
    );
  }

  @Test
  void githubRelease() throws IOException, InterruptedException {
    final Consumer<UpdateChecker> doesNotStartWith =
      (final UpdateChecker updateChecker) ->
        assertThat(updateChecker.getNewVersion())
          .doesNotStartWith("v")
          .doesNotStartWith(".");

    GithubReleaseUpdateChecker githubReleaseUpdateChecker =
      new GithubReleaseUpdateChecker(
        "https://github.com/OpenVPN/easy-rsa/releases",
        ""
      );
    checkUpdate(githubReleaseUpdateChecker);
    doesNotStartWith.accept(githubReleaseUpdateChecker);

    githubReleaseUpdateChecker =
      new GithubReleaseUpdateChecker(
        "https://github.com/adoxa/ansicon/releases",
        ""
      );
    checkUpdate(githubReleaseUpdateChecker);
    doesNotStartWith.accept(githubReleaseUpdateChecker);

    githubReleaseUpdateChecker =
      new GithubReleaseUpdateChecker(
        "https://github.com/drwetter/testssl.sh/releases",
        ""
      );
    checkUpdate(githubReleaseUpdateChecker);
    doesNotStartWith.accept(githubReleaseUpdateChecker);

    githubReleaseUpdateChecker =
      new GithubReleaseUpdateChecker(
        "https://github.com/SpiderLabs/ModSecurity/releases",
        ""
      );
    checkUpdate(githubReleaseUpdateChecker);
    doesNotStartWith.accept(githubReleaseUpdateChecker);

    githubReleaseUpdateChecker =
      new GithubReleaseUpdateChecker(
        "https://github.com/docker/docker-ce/releases",
        ""
      );
    checkUpdate(githubReleaseUpdateChecker);
    doesNotStartWith.accept(githubReleaseUpdateChecker);

    githubReleaseUpdateChecker =
      new GithubReleaseUpdateChecker(
        "https://github.com/gradle/gradle/releases/latest",
        ""
      );
    checkUpdate(githubReleaseUpdateChecker);
    doesNotStartWith.accept(githubReleaseUpdateChecker);

    githubReleaseUpdateChecker =
      new GithubReleaseUpdateChecker(
        "https://github.com/gradle/gradle/releases",
        ""
      );
    checkUpdate(githubReleaseUpdateChecker);
    doesNotStartWith.accept(githubReleaseUpdateChecker);

    githubReleaseUpdateChecker =
      new GithubReleaseUpdateChecker(
        "https://github.com/gradle/gradle/releases/prerelease",
        ""
      );
    checkUpdate(githubReleaseUpdateChecker);
    doesNotStartWith.accept(githubReleaseUpdateChecker);
  }

  @Test
  void xdebug() throws IOException, InterruptedException {
    checkUpdate(new XdebugUpdateChecker("https://xdebug.org/download", ""));
  }

  @Test
  void imageMagickPhpWindows() throws IOException, InterruptedException {
    checkUpdate(
      new ImageMagickPhpWindowsUpdateChecker(
        "https://mlocati.github.io/articles/php-windows-imagick.html",
        ""
      )
    );
  }

  @Test
  void pecl() throws IOException, InterruptedException {
    checkUpdate(new PeclUpdateChecker("https://pecl.php.net/package/ssh2", ""));
  }

  @Test
  void majorGeeks() throws IOException, InterruptedException {
    checkUpdate(
      new MajorGeeksUpdateChecker(
        "https://www.majorgeeks.com/files/details/mozilla_firefox_84.html",
        ""
      )
    );
  }

  @Test
  void nsaneDown() throws IOException, InterruptedException {
    final var nsaneDownUpdateChecker = new NsaneDownUpdateChecker(
      "https://nsaneforums.com/frontpage/internet/web-browsers/mozilla-firefox-browser",
      ""
    );
    nsaneDownUpdateChecker.setName("Mozilla Firefox Browser");
    nsaneDownUpdateChecker.setCookieHolder(cookieHolder);
    nsaneDownUpdateChecker.setCookieHandler(cookieManager);
    checkUpdate(nsaneDownUpdateChecker);
  }

  @Test
  void chromeDriver() throws IOException, InterruptedException {
    checkUpdate(
      new ChromeDriverUpdateChecker(
        "https://googlechromelabs.github.io/chrome-for-testing/last-known-good-versions.json",
        ""
      )
    );
  }

  @Test
  void euroTruckSimulator() throws IOException, InterruptedException {
    final var updateChecker = new EuroTruckSimulatorUpdateChecker(
      System.getProperty("tests.euro-truck-simulator.site-url"),
      ""
    );
    checkUpdate(updateChecker);
  }

  @Test
  void promods() throws IOException, InterruptedException {
    final var updateChecker = new PromodsUpdateChecker(
      "https://www.promods.net/compat.php",
      ""
    );
    updateChecker.setChromeDriverHolder(chromeService);
    checkUpdate(updateChecker);
  }

  @Test
  void polandRebuilding() throws IOException, InterruptedException {
    checkUpdate(
      new PolandRebuildingUpdateChecker(
        "https://polandrebuilding.pl/download/",
        ""
      )
    );
  }

  @Test
  void jfromMetadata() throws IOException, InterruptedException {
    checkUpdate(
      new JfrogMetadataUpdateChecker(
        "https://repo.spring.io/ui/native/release/org/springframework/boot/spring-boot",
        ""
      )
    );
    checkUpdate(
      new JfrogMetadataUpdateChecker(
        "https://repo.spring.io/ui/native/milestone/org/springframework/boot/spring-boot",
        ""
      )
    );
  }

  @Test
  void googleMavenRepository() throws IOException, InterruptedException {
    final var updateChecker = new GoogleMavenRepositoryUpdateChecker(
      "https://maven.google.com/web/index.html#androidx.appcompat:appcompat",
      ""
    );
    checkUpdate(updateChecker);

    assertThat(updateChecker.getNewVersion())
      .doesNotContain("-")
      .doesNotContain("beta")
      .doesNotContain("alpha")
      .doesNotContain("rc");
  }

  @Test
  void quayRepository() throws IOException, InterruptedException {
    checkUpdate(
      new QuayRepositoryUpdateChecker(
        "https://quay.io/repository/keycloak/keycloak?tab=tags&tag=latest",
        ""
      )
    );
  }

  @Test
  void githubDocker() throws IOException, InterruptedException {
    checkUpdate(
      new GithubDockerUpdateChecker(
        "https://github.com/paperless-ngx/paperless-ngx/pkgs/container/paperless-ngx/versions",
        ""
      )
    );
  }

  @Test
  void gpldl() throws IOException, InterruptedException {
    checkUpdate(
      new GpldlUpdateChecker(System.getProperty("tests.gpldl.site-url"), "")
    );
  }

  @Test
  void pythonWindowLibs() throws IOException, InterruptedException {
    final var updateChecker = new PythonWindowLibsUpdateChecker(
      "https://www.lfd.uci.edu/~gohlke/pythonlibs/#pyodbc",
      ""
    );
    updateChecker.setChromeDriverHolder(chromeService);
    checkUpdate(updateChecker);
  }

  @Test
  void jetbrainsCrack() throws IOException, InterruptedException {
    checkUpdate(
      new JetbrainsCrackUpdateChecker(
        System.getProperty("tests.jetbrains-crack.site-url"),
        ""
      )
    );
  }

  @Test
  void wordpressPhp() throws IOException, InterruptedException {
    checkUpdate(
      new WordpressPhpUpdateChecker(
        "https://raw.githubusercontent.com/docker-library/wordpress/master/generate-stackbrew-library.sh",
        ""
      )
    );
  }

  @Test
  void postgres() throws IOException, InterruptedException {
    checkUpdate(
      new PostgresUpdateChecker(
        "https://www.enterprisedb.com/downloads/postgres-postgresql-downloads",
        ""
      )
    );
  }

  @Test
  void openVPN() throws IOException, InterruptedException {
    checkUpdate(
      new OpenVPNUpdateChecker("https://openvpn.net/community-downloads/", "")
    );
  }
}

package com.wallstreetrookie.backend.engine;

import com.wallstreetrookie.backend.model.News;
import com.wallstreetrookie.backend.model.StockModel;
import com.wallstreetrookie.backend.repository.NewsRepository;
import com.wallstreetrookie.backend.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class NewsService {

    // Positive News - einzelne Aktie (Impact +0.02 bis +0.10)
    private static final List<String> POSITIVE_COMPANY = List.of(
            "%s meldet Rekordquartal - Umsatz über Erwartungen",
            "Neues Produkt von %s revolutioniert den Markt",
            "Großauftrag: %s gewinnt Milliardenvertrag",
            "%s übertrifft Analystenerwartungen deutlich",
            "Starke Quartalszahlen treiben %s-Aktie nach oben",
            "%s expandiert in neue Märkte - Investoren begeistert",
            "Strategische Partnerschaft stärkt Position von %s",
            "%s steigert Dividende um 20 Prozent",
            "Innovation bei %s sorgt für Kurssprung",
            "%s meldet Durchbruch in Forschung und Entwicklung",
            "Aktienrückkaufprogramm bei %s angekündigt",
            "%s gewinnt wichtigen Branchenpreis",
            "Analysten stufen %s auf 'Kaufen' hoch",
            "%s verzeichnet Rekord-Kundenwachstum",
            "Patentgenehmigung verschafft %s Wettbewerbsvorteil"
    );

    // Negative News - einzelne Aktie (Impact -0.02 bis -0.10)
    private static final List<String> NEGATIVE_COMPANY = List.of(
            "Gewinnwarnung bei %s - Aktie unter Druck",
            "Produktrückruf bei %s sorgt für Unsicherheit",
            "CEO von %s tritt überraschend zurück",
            "%s verfehlt Quartalsziele - Anleger enttäuscht",
            "Datenleck bei %s erschüttert Kundenvertrauen",
            "%s verliert wichtigen Großkunden",
            "Kartellbehörde leitet Untersuchung gegen %s ein",
            "%s meldet Umsatzrückgang im Kerngeschäft",
            "Qualitätsprobleme belasten %s-Produktion",
            "Klage gegen %s: Millionen-Schadensersatz droht",
            "%s muss Werk vorübergehend schließen",
            "Lieferkettenprobleme treffen %s besonders hart",
            "Analysten senken Kursziel für %s deutlich",
            "%s verliert Marktanteile an Konkurrenz",
            "Insider-Verkäufe bei %s beunruhigen Anleger"
    );

    // Positive Sektor-News (Impact +0.01 bis +0.05)
    private static final List<String> POSITIVE_SECTOR = List.of(
            "Boom im %s-Sektor: Branche wächst stärker als erwartet",
            "Neue Förderungen für %s-Unternehmen angekündigt",
            "%s-Branche profitiert von globalem Trend",
            "Investitionen im %s-Sektor auf Rekordniveau",
            "Regulierung wird gelockert: %s-Sektor atmet auf",
            "Nachfrage im %s-Bereich steigt weltweit",
            "Branchenverband %s meldet positiven Ausblick",
            "%s-Sektor übertrifft Gesamtmarkt deutlich"
    );

    // Negative Sektor-News (Impact -0.01 bis -0.05)
    private static final List<String> NEGATIVE_SECTOR = List.of(
            "%s-Sektor unter Druck: Neue Regulierung angekündigt",
            "Branchenkrise im %s-Bereich verschärft sich",
            "Überkapazitäten belasten %s-Sektor",
            "Preisverfall trifft %s-Unternehmen hart",
            "Handelsstreit belastet %s-Branche",
            "Strenge Auflagen für %s-Sektor geplant",
            "%s-Branche kämpft mit Fachkräftemangel",
            "Konjunktursorgen drücken %s-Aktien"
    );

    // Positive globale News (Impact +0.005 bis +0.02)
    private static final List<String> POSITIVE_GLOBAL = List.of(
            "Handelsabkommen sorgt für Optimismus an den Märkten",
            "Zentralbank signalisiert lockere Geldpolitik",
            "Konjunkturdaten übertreffen Erwartungen",
            "Globales Wirtschaftswachstum beschleunigt sich",
            "Inflation sinkt: Märkte reagieren positiv",
            "Neues Konjunkturpaket der Regierung angekündigt"
    );

    // Negative globale News (Impact -0.005 bis -0.02)
    private static final List<String> NEGATIVE_GLOBAL = List.of(
            "Zentralbank erhöht Zinsen überraschend",
            "Rezessionsängste belasten die Börsen",
            "Geopolitische Spannungen sorgen für Unsicherheit",
            "Inflation steigt stärker als erwartet",
            "Arbeitsmarktdaten enttäuschen - Märkte fallen",
            "Handelskrieg droht: Neue Zölle angekündigt"
    );

    private final StockRepository stockRepository;
    private final NewsRepository newsRepository;
    private final Random random = new Random();

    public List<News> generateNews(String gameSessionId) {
        List<News> generatedNews = new ArrayList<>();
        int newsCount = 1 + random.nextInt(3);

        for (int i = 0; i < newsCount; i++) {
            int type = random.nextInt(100);
            News news;

            if (type < 50) {
                news = generateCompanyNews(gameSessionId);
            } else if (type < 80) {
                news = generateSectorNews(gameSessionId);
            } else {
                news = generateGlobalNews(gameSessionId);
            }

            news = newsRepository.save(news);
            generatedNews.add(news);
        }

        return generatedNews;
    }

    private News generateCompanyNews(String gameSessionId) {
        List<StockModel> stocks = stockRepository.findAll();
        StockModel stock = stocks.get(random.nextInt(stocks.size()));
        boolean positive = random.nextBoolean();

        List<String> templates = positive ? POSITIVE_COMPANY : NEGATIVE_COMPANY;
        String title = String.format(templates.get(random.nextInt(templates.size())), stock.getName());
        double impact = positive
                ? 0.02 + random.nextDouble() * 0.08
                : -(0.02 + random.nextDouble() * 0.08);

        return News.builder()
                .title(title)
                .content(title + ".")
                .affectedSymbols(List.of(stock.getSymbol()))
                .affectedSector(stock.getSector())
                .impact(impact)
                .timestamp(Instant.now())
                .gameSessionId(gameSessionId)
                .build();
    }

    private News generateSectorNews(String gameSessionId) {
        List<String> sectors = List.of("Technologie", "Finanzen", "Gesundheit", "Energie", "Konsum", "Industrie", "Krypto/FinTech");
        String sector = sectors.get(random.nextInt(sectors.size()));
        boolean positive = random.nextBoolean();

        List<String> templates = positive ? POSITIVE_SECTOR : NEGATIVE_SECTOR;
        String title = String.format(templates.get(random.nextInt(templates.size())), sector);
        double impact = positive
                ? 0.01 + random.nextDouble() * 0.04
                : -(0.01 + random.nextDouble() * 0.04);

        List<String> sectorSymbols = stockRepository.findBySector(sector).stream()
                .map(StockModel::getSymbol)
                .toList();

        return News.builder()
                .title(title)
                .content(title + ".")
                .affectedSymbols(sectorSymbols)
                .affectedSector(sector)
                .impact(impact)
                .timestamp(Instant.now())
                .gameSessionId(gameSessionId)
                .build();
    }

    private News generateGlobalNews(String gameSessionId) {
        boolean positive = random.nextBoolean();

        List<String> templates = positive ? POSITIVE_GLOBAL : NEGATIVE_GLOBAL;
        String title = templates.get(random.nextInt(templates.size()));
        double impact = positive
                ? 0.005 + random.nextDouble() * 0.015
                : -(0.005 + random.nextDouble() * 0.015);

        List<String> allSymbols = stockRepository.findAll().stream()
                .map(StockModel::getSymbol)
                .toList();

        return News.builder()
                .title(title)
                .content(title + ".")
                .affectedSymbols(allSymbols)
                .impact(impact)
                .timestamp(Instant.now())
                .gameSessionId(gameSessionId)
                .build();
    }
}

package yourstyle.com.shope.controller.site.products;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import yourstyle.com.shope.model.Account;
import yourstyle.com.shope.model.Product;
import yourstyle.com.shope.model.SearchHistory;
import yourstyle.com.shope.repository.AccountRepository;
import yourstyle.com.shope.repository.SearchHistoryRepository;
import yourstyle.com.shope.service.ProductService;

@Controller
public class SearchProductController {
    @Autowired
    private SearchHistoryRepository searchHistoryRepository;

    @Autowired
    ProductService productService;

    @Autowired
    private AccountRepository accountRepository;

    @GetMapping("/search")
    @Transactional
    public String searchProductByName(@RequestParam("name") String name, Model model) {
        List<Product> products = productService.findByNameContainingIgnoreCase(name);
        model.addAttribute("products", products);
        model.addAttribute("searchTerm", name);

        if (SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            Optional<Account> accountOptional = accountRepository.findByUsername(username);

            accountOptional.ifPresent(account -> {
                // Lấy danh sách lịch sử tìm kiếm của tài khoản theo thứ tự thời gian từ cũ đến
                // mới
                List<SearchHistory> histories = searchHistoryRepository
                        .findTop10ByAccountOrderBySearchTimeDesc(account);

                // Kiểm tra nếu từ khóa tìm kiếm đã có trong lịch sử
                boolean alreadyExists = histories.stream()
                        .anyMatch(history -> history.getKeyword().equalsIgnoreCase(name));

                if (!alreadyExists) {
                    if (histories.size() >= 10) {
                        // Xóa lịch sử cũ nhất nếu có từ 10 mục trở lên
                        searchHistoryRepository.delete(histories.get(0));
                    }

                    // Lưu lịch sử tìm kiếm mới
                    SearchHistory history = new SearchHistory();
                    history.setAccount(account);
                    history.setKeyword(name);
                    searchHistoryRepository.save(history);
                }
            });
        }

        return "site/products/allproduct";
    }

    @GetMapping("/searchHistory")
    @ResponseBody
    public List<SearchHistory> getSearchHistory() {
        if (SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            Optional<Account> accountOptional = accountRepository.findByUsername(username);

            if (accountOptional.isPresent()) {
                return searchHistoryRepository.findTop10ByAccountOrderBySearchTimeDesc(accountOptional.get());
            }
        }
        return Collections.emptyList();
    }

    @GetMapping(value = "/productSuggestions", produces = "application/json")
    @ResponseBody
    public List<Map<String, Object>> getSuggestions(@RequestParam("name") String name) {
        return productService.findByNameContainingIgnoreCase(name)
                .stream()
                .map(product -> {
                    Map<String, Object> suggestion = new HashMap<>();
                    suggestion.put("productId", product.getProductId());
                    suggestion.put("name", product.getName());
                    return suggestion;
                })
                .collect(Collectors.toList());
    }

    @GetMapping("/deleteSearchHistory")
    @ResponseBody
    public String deleteSearchHistory(@RequestParam("id") Integer id) {
        searchHistoryRepository.deleteById(id);
        return "Deleted";
    }

    @PostMapping("/addSearchHistory")
    @ResponseBody
    @Transactional
    public String addSearchHistory(@RequestParam("keyword") String keyword) {
        if (SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            Optional<Account> accountOptional = accountRepository.findByUsername(username);

            accountOptional.ifPresent(account -> {
                // Lấy danh sách lịch sử tìm kiếm của tài khoản
                List<SearchHistory> histories = searchHistoryRepository
                        .findTop10ByAccountOrderBySearchTimeDesc(account);

                // Kiểm tra nếu từ khóa tìm kiếm đã có trong lịch sử
                boolean alreadyExists = histories.stream()
                        .anyMatch(history -> history.getKeyword().equalsIgnoreCase(keyword));

                if (!alreadyExists) {
                    // Xóa lịch sử cũ nhất nếu có từ 10 mục trở lên
                    if (histories.size() >= 10) {
                        searchHistoryRepository.delete(histories.get(0));
                    }

                    // Lưu lịch sử tìm kiếm mới
                    SearchHistory history = new SearchHistory();
                    history.setAccount(account);
                    history.setKeyword(keyword);
                    searchHistoryRepository.save(history);
                }
            });
        }

        return "Search history added";
    }

}

